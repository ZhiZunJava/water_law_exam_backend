package org.can.water_law_exam_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.entity.Role;
import org.can.water_law_exam_backend.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Slf4j
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 1.7 获取用户角色列表（不包含管理员角色）
     * 请求路径：/role/list
     * 请求方式：GET
     *
     * @return 角色列表
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public Result<List<Role>> getRoleList() {
        log.info("获取角色列表请求");
        List<Role> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
}

