package org.can.water_law_exam_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.common.constant.ResultCodeEnum;
import org.can.water_law_exam_backend.dto.request.accountuser.AccountUserAddRequest;
import org.can.water_law_exam_backend.dto.request.accountuser.AccountUserPageRequest;
import org.can.water_law_exam_backend.dto.request.accountuser.AccountUserUpdateRequest;
import org.can.water_law_exam_backend.dto.request.accountuser.SetRoleRequest;
import org.can.water_law_exam_backend.dto.response.accountuser.AccountUserVO;
import org.can.water_law_exam_backend.dto.response.common.PageResult;
import org.can.water_law_exam_backend.service.AccountUserService;
import org.can.water_law_exam_backend.service.RoleService;
import org.can.water_law_exam_backend.service.TokenService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学员管理控制器
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Slf4j
@RestController
@RequestMapping("/au")
@RequiredArgsConstructor
public class AccountUserController {

    private final AccountUserService accountUserService;
    private final RoleService roleService;
    private final TokenService tokenService;

    /**
     * 1.1 添加学员
     * 请求路径：/au/add
     * 请求方式：POST
     *
     * @param request 请求
     * @return {@link Result }<{@link Long }>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result<Long> addAccountUser(@Valid @RequestBody AccountUserAddRequest request) {
        log.info("添加学员请求：name={}, idNo={}", request.getName(), request.getIdNo());
        Long id = accountUserService.addAccountUser(request);
        return Result.success(id);
    }

    /**
     * 1.2 修改学员信息
     * 请求路径：/au/update
     * 请求方式：POST
     *
     * @param request 请求
     * @return {@link Result }<{@link String }>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public Result<String> updateAccountUser(@Valid @RequestBody AccountUserUpdateRequest request) {
        log.info("修改学员请求：id={}, name={}", request.getId(), request.getName());
        accountUserService.updateAccountUser(request);
        return Result.success("修改成功");
    }

    /**
     * 1.3 批量删除学员
     * 请求路径：/au/delete
     * 请求方式：POST
     *
     * @param ids 标识符
     * @return {@link Result }<{@link String }>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<String> deleteAccountUsers(@RequestBody List<Long> ids) {
        log.info("批量删除学员请求：ids={}", ids);
        accountUserService.deleteAccountUsers(ids);
        return Result.success("删除成功");
    }

    /**
     * 1.4 "禁用|取消禁用"学员
     * 请求路径：/au/lock/{id}
     * 请求方式：POST
     *
     * @param id 学员 ID
     * @return 设置成功后的用户状态：true-禁用，false-正常
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/lock/{id}")
    public Result<Boolean> toggleLock(@PathVariable Long id) {
        log.info("切换学员禁用状态请求：id={}", id);
        if (id == null || id <= 0) {
            return Result.error(1, "学员ID必须大于0");
        }
        Boolean locked = accountUserService.toggleLock(id);
        String msg = locked ? "已禁用" : "已启用";
        return Result.success(msg, locked);
    }

    /**
     * 1.5 获取学员信息列表-分页
     * 请求路径：/au/pages
     * 请求方式：GET
     *
     * @param request 分页查询请求
     * @return 分页结果
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pages")
    public Result<PageResult<AccountUserVO>> getAccountUsersByPage(@Valid @RequestBody AccountUserPageRequest request) {
        log.info("分页查询学员列表请求：page={}, size={}", request.getPage(), request.getSize());
        PageResult<AccountUserVO> result = accountUserService.getAccountUsersByPage(request);
        return Result.success(result);
    }

    /**
     * 1.6 获取单个学员基本信息
     * 请求路径：/au/{id}
     * 请求方式：GET
     *
     * @param id 学员 ID
     * @return 学员信息
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Result<AccountUserVO> getAccountUserById(@PathVariable Long id) {
        log.info("查询学员信息请求：id={}", id);
        AccountUserVO vo = accountUserService.getAccountUserById(id);
        return Result.success(vo);
    }

    /**
     * 1.8 设置前台用户的用户角色
     * 请求路径：/au/setRole
     * 请求方式：POST
     *
     * @param request 设置角色请求
     * @return 成功响应
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/setRole")
    public Result<String> setUserRole(@Valid @RequestBody SetRoleRequest request) {
        log.info("设置用户角色请求：uid={}, rid={}", request.getUid(), request.getRid());
        roleService.setUserRole(request.getUid(), request.getRid());
        return Result.success("设置角色成功");
    }

    /**
     * 1.9 移除前台用户角色
     * 请求路径：/au/removeRole
     * 请求方式：POST
     *
     * @param request 移除角色请求
     * @return 成功响应
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/removeRole")
    public Result<String> removeUserRole(@Valid @RequestBody SetRoleRequest request) {
        log.info("移除用户角色请求：uid={}, rid={}", request.getUid(), request.getRid());
        roleService.removeUserRole(request.getUid(), request.getRid());
        return Result.success("移除角色成功");
    }

    /**
     * 强制用户下线（撤销用户token）
     * 请求路径：/au/forceLogout/{id}
     * 请求方式：POST
     *
     * @param id 用户 ID
     * @return 成功响应
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/forceLogout/{id}")
    public Result<String> forceLogout(@PathVariable Long id) {
        log.info("强制用户下线请求：id={}", id);
        if (id == null || id <= 0) {
            return Result.error(1, "用户ID必须大于0");
        }
        tokenService.forceLogout(id);
        return Result.success("用户已被强制下线");
    }
}

