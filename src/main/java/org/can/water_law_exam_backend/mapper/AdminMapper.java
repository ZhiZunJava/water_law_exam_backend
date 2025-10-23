package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.Admin;

/**
 * 管理员Mapper接口
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
}


