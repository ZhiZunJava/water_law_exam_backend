package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.Organization;

import java.util.List;

/**
 * 单位Mapper接口
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Mapper
public interface OrganizationMapper {

    /**
     * 根据父级ID和启用状态获取下一级组织列表
     *
     * @param pId     父级组织ID（0表示顶级组织）
     * @param enabled 是否启用（null表示所有）
     * @return 组织列表
     */
    List<Organization> selectByParentIdAndEnabled(@Param("pId") Long pId, @Param("enabled") Boolean enabled);

    /**
     * 根据城市ID获取单位列表
     *
     * @param cityId 城市ID
     * @return 单位列表
     */
    List<Organization> selectByCityId(@Param("cityId") Integer cityId);

    /**
     * 根据城市ID和启用状态获取单位列表
     *
     * @param cityId  城市ID
     * @param enabled 是否启用（null表示所有）
     * @return 单位列表
     */
    List<Organization> selectByCityIdAndEnabled(@Param("cityId") Integer cityId, @Param("enabled") Boolean enabled);

    /**
     * 分页查询单位列表
     *
     * @param cId    城市ID（可选）
     * @param key    搜索关键字（单位名称）
     * @return 单位列表
     */
    List<Organization> selectByPage(@Param("cId") Integer cId, @Param("key") String key);

    /**
     * 根据ID查询单位
     *
     * @param id 单位ID
     * @return 单位信息
     */
    Organization selectById(@Param("id") Long id);

    /**
     * 根据单位名称和城市ID查询单位（用于检查重复）
     *
     * @param orgName 单位名称
     * @param cityId  城市ID
     * @return 单位信息
     */
    Organization selectByNameAndCityId(@Param("orgName") String orgName, @Param("cityId") Integer cityId);

    /**
     * 插入单位
     *
     * @param organization 单位信息
     * @return 影响行数
     */
    int insert(Organization organization);

    /**
     * 更新单位
     *
     * @param organization 单位信息
     * @return 影响行数
     */
    int update(Organization organization);

    /**
     * 根据ID删除单位
     *
     * @param id 单位ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除单位
     *
     * @param ids 单位ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 统计单位下的学员数量
     *
     * @param orgId 单位ID
     * @return 学员数量
     */
    int countUsersByOrgId(@Param("orgId") Long orgId);
}

