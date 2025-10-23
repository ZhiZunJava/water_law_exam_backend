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
    private final CaptchaService captchaService;

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
        // 1. 验证验证码
        boolean captchaValid = captchaService.verifyCaptcha(
                request.getCaptchaId(),
                request.getCaptchaCode()
        );
        if (!captchaValid) {
            log.warn("验证码验证失败：用户={}, 验证码ID={}", request.getUsername(), request.getCaptchaId());
            throw new BusinessException(400, "验证码错误或已过期");
        }

        try {
            // 2. 构造用户名（格式：userType:username）
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
            log.warn("认证失败：用户={}, 类型={}, 原因={}", request.getUsername(), userType, e.getMessage());
            throw new BusinessException(401, "用户名或密码错误");
        } catch (BusinessException e) {
            // 重新抛出业务异常，保持原有的错误码和消息
            throw e;
        } catch (Exception e) {
            log.error("登录异常：用户={}, 类型={}", request.getUsername(), userType, e);
            throw new BusinessException(500, "登录失败，请稍后重试");
        }
    }
}

