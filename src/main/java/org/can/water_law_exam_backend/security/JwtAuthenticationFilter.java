package org.can.water_law_exam_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.config.JwtProperties;
import org.can.water_law_exam_backend.service.TokenService;
import org.can.water_law_exam_backend.util.JwtUtil;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT认证过滤器
 * 拦截所有请求，验证JWT Token的有效性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 获取请求头中的Authorization
        String authHeader = request.getHeader(jwtProperties.getHeader());

        // 如果没有Authorization头或者格式不对，直接放行（让Security的其他过滤器处理）
        if (authHeader == null || !authHeader.startsWith(jwtProperties.getTokenPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 提取Token（去除前缀）
            String token = authHeader.substring(jwtProperties.getTokenPrefix().length());

            // 1. 验证Token格式和签名
            if (!jwtUtil.validateToken(token)) {
                handleAuthenticationFailure(response, "Token无效或已过期");
                return;
            }

            // 2. 验证Token是否在Redis中存在（实现token撤销功能）
            if (!tokenService.validateToken(token)) {
                handleAuthenticationFailure(response, "Token已失效，请重新登录");
                return;
            }

            // 从Token中提取用户信息
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            String userType = jwtUtil.getUserTypeFromToken(token);

            if (userId == null || username == null || userType == null) {
                handleAuthenticationFailure(response, "Token信息不完整");
                return;
            }

            // 根据用户类型构造权限列表
            List<GrantedAuthority> authorities = new ArrayList<>();
            if ("admin".equals(userType)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }

            // 构造认证对象
            LoginUser loginUser = new LoginUser(userId, username, null, null, userType, authorities);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 将认证信息设置到SecurityContext中
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            log.error("Token处理异常：", e);
            handleAuthenticationFailure(response, "Token处理异常");
            return;
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 处理认证失败
     */
    private void handleAuthenticationFailure(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<Void> result = Result.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}


