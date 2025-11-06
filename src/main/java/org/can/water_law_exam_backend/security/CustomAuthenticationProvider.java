package org.can.water_law_exam_backend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自定义认证提供者
 * 支持多种登录方式：
 * 1. 管理员账号密码登录（admin:账号）
 * 2. 学员身份证号密码登录（user:身份证号）
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 执行认证
     *
     * @param authentication 认证信息
     * @return 认证结果
     * @throws AuthenticationException 认证异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取认证信息
        String principal = (String) authentication.getPrincipal();
        String credentials = (String) authentication.getCredentials();

        try {
            // 判断登录方式
            if (!principal.startsWith("admin:") && !principal.startsWith("user:")) {
                log.error("不支持的登录方式：{}", principal);
                throw new BadCredentialsException("不支持的登录方式");
            }

            // 账号密码登录
            log.debug("账号密码登录：{}", principal);
            UserDetails userDetails = userDetailsService.loadUserByUsername(principal);

            // 验证密码
            if (!passwordEncoder.matches(credentials, userDetails.getPassword())) {
                log.error("账号密码登录密码错误：{}", principal);
                throw new BadCredentialsException("用户名或密码错误");
            }

            log.info("用户认证成功：{}", principal);

            // 创建已认证的令牌
            UsernamePasswordAuthenticationToken authenticatedToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            credentials,
                            userDetails.getAuthorities()
                    );
            authenticatedToken.setDetails(authentication.getDetails());

            return authenticatedToken;

        } catch (AuthenticationException e) {
            log.error("认证失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("认证异常：", e);
            throw new BadCredentialsException("认证失败");
        }
    }

    /**
     * 判断是否支持该认证类型
     *
     * @param authentication 认证类型
     * @return 是否支持
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

