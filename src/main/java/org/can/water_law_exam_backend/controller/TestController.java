package org.can.water_law_exam_backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.security.LoginUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于测试认证和授权功能
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 测试认证 - 需要登录
     *
     * @param loginUser 当前登录用户
     * @return 用户信息
     */
    @GetMapping("/auth")
    public Result<Map<String, Object>> testAuth(@AuthenticationPrincipal LoginUser loginUser) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", loginUser.getUserId());
        userInfo.put("username", loginUser.getUsername());
        userInfo.put("name", loginUser.getName());
        userInfo.put("userType", loginUser.getUserType());
        userInfo.put("enabled", loginUser.isEnabled());
        
        return Result.success("认证测试成功", userInfo);
    }

    /**
     * 测试管理员权限
     *
     * @param loginUser 当前登录用户
     * @return 成功信息
     */
    @GetMapping("/admin")
    public Result<String> testAdmin(@AuthenticationPrincipal LoginUser loginUser) {
        if (!"admin".equals(loginUser.getUserType())) {
            return Result.error(403, "需要管理员权限");
        }
        return Result.success("管理员权限测试成功", "欢迎，管理员：" + loginUser.getName());
    }

    /**
     * 测试学员权限
     *
     * @param loginUser 当前登录用户
     * @return 成功信息
     */
    @GetMapping("/user")
    public Result<String> testUser(@AuthenticationPrincipal LoginUser loginUser) {
        if (!"user".equals(loginUser.getUserType())) {
            return Result.error(403, "需要学员权限");
        }
        return Result.success("学员权限测试成功", "欢迎，学员：" + loginUser.getName());
    }
}



