package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.AccountUser;

/**
 * 学员Mapper接口
 */
@Mapper
public interface AccountUserMapper {

    /**
     * 根据身份证号查询学员
     *
     * @param idNo 身份证号
     * @return 学员信息
     */
    AccountUser selectByIdNo(@Param("idNo") String idNo);

    /**
     * 根据身份证号查询学员（别名方法，用于UserDetailsService）
     *
     * @param idNo 身份证号
     * @return 学员信息
     */
    default AccountUser findByIdNo(String idNo) {
        return selectByIdNo(idNo);
    }

    /**
     * 根据ID查询学员
     *
     * @param id 学员ID
     * @return 学员信息
     */
    AccountUser selectById(@Param("id") Long id);

    /**
     * 更新学员密码
     *
     * @param id 学员ID
     * @param pwd 新密码（加密后）
     * @return 影响行数
     */
    int updatePassword(@Param("id") Long id, @Param("pwd") String pwd);
}


