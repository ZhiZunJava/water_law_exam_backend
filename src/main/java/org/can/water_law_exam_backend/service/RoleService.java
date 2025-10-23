package org.can.water_law_exam_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.entity.Role;
import org.can.water_law_exam_backend.entity.UserRole;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.AccountUserMapper;
import org.can.water_law_exam_backend.mapper.RoleMapper;
import org.can.water_law_exam_backend.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final AccountUserMapper accountUserMapper;

    /**
     * 获取所有可授予前台用户的角色列表
     *
     * @return 角色列表
     */
    public List<Role> getAllRoles() {
        return roleMapper.selectAll();
    }

    /**
     * 为用户设置角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void setUserRole(Long userId, Integer roleId) {
        // 检查用户是否存在
        if (accountUserMapper.selectById(userId) == null) {
            throw new BusinessException(1, "用户不存在");
        }

        // 检查角色是否存在
        if (roleMapper.selectById(roleId) == null) {
            throw new BusinessException(1, "角色不存在");
        }

        // 防止分配管理员角色
        if (roleId == 1) {
            throw new BusinessException(1, "不允许分配管理员角色");
        }

        // 检查用户是否已拥有该角色
        int count = userRoleMapper.existsByUserIdAndRoleId(userId, roleId);
        if (count > 0) {
            throw new BusinessException(1, "用户已拥有该角色");
        }

        // 创建用户角色关联
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);

        int rows = userRoleMapper.insert(userRole);
        if (rows == 0) {
            throw new BusinessException(1, "设置用户角色失败");
        }

        log.info("设置用户角色成功：userId={}, roleId={}", userId, roleId);
    }

    /**
     * 移除用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRole(Long userId, Integer roleId) {
        // 检查用户是否存在
        if (accountUserMapper.selectById(userId) == null) {
            throw new BusinessException(1, "用户不存在");
        }

        // 检查角色是否存在
        if (roleMapper.selectById(roleId) == null) {
            throw new BusinessException(1, "角色不存在");
        }

        // 检查用户是否拥有该角色
        int count = userRoleMapper.existsByUserIdAndRoleId(userId, roleId);
        if (count == 0) {
            throw new BusinessException(1, "用户未拥有该角色");
        }

        // 删除用户角色关联
        int rows = userRoleMapper.delete(userId, roleId);
        if (rows == 0) {
            throw new BusinessException(1, "移除用户角色失败");
        }

        log.info("移除用户角色成功：userId={}, roleId={}", userId, roleId);
    }

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<Role> getUserRoles(Long userId) {
        return roleMapper.selectByUserId(userId);
    }
}

