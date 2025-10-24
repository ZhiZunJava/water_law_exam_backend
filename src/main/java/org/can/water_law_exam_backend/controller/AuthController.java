package org.can.water_law_exam_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.config.JwtProperties;
import org.can.water_law_exam_backend.dto.request.auth.LoginRequest;
import org.can.water_law_exam_backend.dto.request.auth.RegisterRequest;
import org.can.water_law_exam_backend.dto.response.auth.CurrentUserResponse;
import org.can.water_law_exam_backend.dto.response.auth.LoginResponse;
import org.can.water_law_exam_backend.dto.response.auth.RegisterResponse;
import org.can.water_law_exam_backend.service.AuthService;
import org.can.water_law_exam_backend.service.TokenService;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理登录、登出等认证相关请求
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final JwtProperties jwtProperties;

    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/admin/login")
    public Result<LoginResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        log.info("管理员登录请求：{}", request.getUsername());
        LoginResponse response = authService.adminLogin(request);
        return Result.success("登录成功", response);
    }

    /**
     * 学员登录（身份证号+密码）
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/user/login")
    public Result<LoginResponse> userLogin(@Valid @RequestBody LoginRequest request) {
        log.info("学员登录请求：{}", request.getUsername());
        LoginResponse response = authService.userLogin(request);
        return Result.success("登录成功", response);
    }

    /**
     * 获取当前登录用户详细信息
     *
     * @param request HTTP请求
     * @return 当前用户详细信息
     */
    @GetMapping("/current")
    public Result<CurrentUserResponse> getCurrentUser(HttpServletRequest request) {
        // 获取请求头中的Token
        String authHeader = request.getHeader(jwtProperties.getHeader());
        
        if (authHeader == null || !authHeader.startsWith(jwtProperties.getTokenPrefix())) {
            log.warn("获取当前用户信息失败：Token不存在");
            return Result.error(401, "未提供认证Token");
        }
        
        // 提取Token（去除前缀）
        String token = authHeader.substring(jwtProperties.getTokenPrefix().length());
        
        // 获取当前用户详细信息
        CurrentUserResponse userInfo = authService.getCurrentUser(token);
        
        log.info("获取当前用户信息成功：用户ID={}, 类型={}", userInfo.getId(), userInfo.getUserType());
        
        return Result.success("获取成功", userInfo);
    }

    /**
     * 登出（从Redis删除Token）
     * 无论token是否存在或过期，都返回成功，确保用户体验
     *
     * @param request HTTP请求
     * @return 成功响应
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        try {
            // 获取请求头中的Token
            String authHeader = request.getHeader(jwtProperties.getHeader());
            
            if (authHeader == null || authHeader.trim().isEmpty()) {
                log.info("登出请求：未提供Token，直接返回成功");
                return Result.success("登出成功");
            }
            
            if (!authHeader.startsWith(jwtProperties.getTokenPrefix())) {
                log.warn("登出请求：Token格式错误，但仍返回成功");
                return Result.success("登出成功");
            }
            
            // 提取Token（去除前缀）
            String token = authHeader.substring(jwtProperties.getTokenPrefix().length()).trim();
            
            if (token.isEmpty()) {
                log.info("登出请求：Token为空，直接返回成功");
                return Result.success("登出成功");
            }
            
            // 尝试从Redis中删除Token（即使token已过期或不存在，删除操作也不会报错）
            tokenService.removeToken(token);
            
            log.info("用户登出成功，Token已从Redis删除");
            return Result.success("登出成功");
            
        } catch (Exception e) {
            // 即使发生异常，也返回成功，因为登出的目的就是让用户退出
            log.warn("登出过程发生异常，但仍返回成功：{}", e.getMessage());
            return Result.success("登出成功");
        }
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册响应
     */
    @PostMapping("/register")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册请求：姓名={}, 身份证号={}, 手机号={}", 
                request.getName(), request.getIdNo(), request.getPhone());
        RegisterResponse response = authService.register(request);
        return Result.success("注册成功", response);
    }
}


