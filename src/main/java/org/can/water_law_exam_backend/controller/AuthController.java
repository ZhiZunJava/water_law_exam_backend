package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.request.auth.LoginRequest;
import org.can.water_law_exam_backend.dto.response.auth.LoginResponse;
import org.can.water_law_exam_backend.service.AuthService;
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
     * 登出（客户端删除Token即可）
     *
     * @return 成功响应
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        log.info("用户登出");
        return Result.success("登出成功");
    }

    /**
     * 健康检查
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("系统运行正常");
    }
}


