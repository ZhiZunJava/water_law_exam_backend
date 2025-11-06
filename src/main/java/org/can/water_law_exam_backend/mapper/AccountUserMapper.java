package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.AccountUser;

import java.util.List;

/**
 * 学员Mapper接口
 *
 * @author 程安宁
 * @date 2025/11/06
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

    /**
     * 插入学员信息
     *
     * @param accountUser 学员信息
     * @return 影响行数
     */
    int insert(AccountUser accountUser);

    /**
     * 更新学员信息
     *
     * @param accountUser 学员信息
     * @return 影响行数
     */
    int update(AccountUser accountUser);

    /**
     * 根据ID删除学员
     *
     * @param id 学员ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除学员
     *
     * @param ids 学员ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 切换学员禁用状态
     *
     * @param id 学员ID
     * @return 影响行数
     */
    int toggleLock(@Param("id") Long id);

    /**
     * 分页查询学员列表（使用PageHelper，不需要offset和limit参数）
     *
     * @param orgId 单位ID（可选）
     * @param key 检索关键字（可选）
     * @return 学员列表
     */
    List<AccountUser> selectByPage(@Param("orgId") Long orgId, @Param("key") String key);

    /**
     * 根据手机号查询学员
     *
     * @param phone 手机号
     * @return 学员信息
     */
    AccountUser selectByPhone(@Param("phone") String phone);

    /**
     * 根据单位ID查询单位名称
     *
     * @param orgId 单位ID
     * @return 单位名称
     */
    String selectOrgNameById(@Param("orgId") Long orgId);
}


