package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.Admin;

import java.util.List;

/**
 * 管理员Mapper接口
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Mapper
public interface AdminMapper {

    /**
     * 根据登录账号查询管理员
     *
     * @param userNo 登录账号
     * @return 管理员信息
     */
    Admin selectByUserNo(@Param("userNo") String userNo);

    /**
     * 根据ID查询管理员
     *
     * @param id 管理员ID
     * @return 管理员信息
     */
    Admin selectById(@Param("id") Long id);

    /**
     * 更新管理员密码
     *
     * @param id 管理员ID
     * @param pwd 新密码（加密后）
     * @return 影响行数
     */
    int updatePassword(@Param("id") Long id, @Param("pwd") String pwd);

    /**
     * 分页查询管理员（按关键字匹配 name 或 user_no）
     *
     * @param key 关键字，可为空
     * @return 管理员列表
     */
    List<Admin> selectByPage(@Param("key") String key);

    /**
     * 插入管理员
     *
     * @param admin 管理员
     * @return int
     */
    int insertAdmin(Admin admin);

    /**
     * 更新管理员基础信息（name, user_no）
     *
     * @param id 主键
     * @param name 名称
     * @param userNo 账号
     * @return 影响行数
     */
    int updateBase(@Param("id") Long id, @Param("name") String name, @Param("userNo") String userNo);

    /**
     * 统计相同账号（排除自身）
     *
     * @param userNo 账号
     * @param excludeId 排除ID
     * @return 数量
     */
    int countByUserNoExcludeId(@Param("userNo") String userNo, @Param("excludeId") Long excludeId);

    /**
     * 批量删除
     *
     * @param ids ID列表
     * @return 条数
     */
    int deleteBatch(@Param("ids") java.util.List<Long> ids);

    /**
     * 切换锁定状态（locked = NOT locked）
     *
     * @param id ID
     * @return 条数
     */
    int toggleLocked(@Param("id") Long id);
}


