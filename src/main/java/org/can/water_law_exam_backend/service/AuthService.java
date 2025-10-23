package org.can.water_law_exam_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.config.JwtProperties;
import org.can.water_law_exam_backend.dto.LoginRequest;
import org.can.water_law_exam_backend.dto.LoginResponse;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.security.LoginUser;
import org.can.water_law_exam_backend.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * 认证服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @return 登录响应（包含Token）
     */
    public LoginResponse adminLogin(LoginRequest request) {
        return login(request, "admin");
    }

    /**
     * 学员登录（身份证号+密码）
     *
     * @param request 登录请求
     * @return 登录响应（包含Token）
     */
    public LoginResponse userLogin(LoginRequest request) {
        return login(request, "user");
    }

    /**
     * 通用登录逻辑
     *
     * @param request  登录请求
     * @param userType 用户类型（admin或user）
     * @return 登录响应
     */
    private LoginResponse login(LoginRequest request, String userType) {
        try {
            // 构造用户名（格式：userType:username）
            // 例如：admin:admin 或 user:身份证号
            String fullUsername = userType + ":" + request.getUsername();

            // 创建认证Token
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(fullUsername, request.getPassword());

            // 执行认证
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 认证成功，获取用户信息
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            // 生成JWT Token
            String token = jwtUtil.generateToken(
                    loginUser.getUserId(),
                    loginUser.getUsername(),
                    loginUser.getUserType()
            );

            log.info("用户登录成功：{}, 类型：{}", loginUser.getUsername(), userType);

            // 返回登录响应
            return new LoginResponse(
                    token,
                    loginUser.getUserId(),
                    loginUser.getUsername(),
                    loginUser.getName(),
                    loginUser.getUserType(),
                    jwtProperties.getTokenPrefix(),
                    jwtProperties.getExpiration()
            );

        } catch (AuthenticationException e) {
            log.warn("登录失败：{}", request.getUsername());
            throw new BusinessException(401, "用户名或密码错误");
        } catch (Exception e) {
            log.error("登录异常：", e);
            throw new BusinessException("登录失败，请稍后重试");
        }
    }
}

