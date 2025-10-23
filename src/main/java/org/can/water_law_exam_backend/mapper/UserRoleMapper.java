package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.UserRole;

/**
 * 用户角色关联Mapper接口
 */
@Mapper
public interface UserRoleMapper {

    /**
     * 插入用户角色关联
     *
     * @param userRole 用户角色关联信息
     * @return 影响行数
     */
    int insert(UserRole userRole);

    /**
     * 删除用户角色关联
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    int delete(@Param("userId") Long userId, @Param("roleId") Integer roleId);

    /**
     * 根据用户ID删除所有角色关联
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否存在
     */
    int existsByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Integer roleId);
}


