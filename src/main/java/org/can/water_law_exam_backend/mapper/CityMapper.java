package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.City;

import java.util.List;

/**
 * 城市Mapper接口
 */
@Mapper
public interface CityMapper {

    /**
     * 查询所有城市列表
     *
     * @return 城市列表
     */
    List<City> selectAll();

    /**
     * 分页查询城市列表
     *
     * @param offset 偏移量
     * @param limit  每页数量
     * @param key    城市名称关键字（可选，用于模糊查询）
     * @return 城市列表
     */
    List<City> selectByPage(@Param("offset") int offset, 
                           @Param("limit") int limit, 
                           @Param("key") String key);

    /**
     * 统计城市总数
     *
     * @param key 城市名称关键字（可选，用于模糊查询）
     * @return 城市总数
     */
    long countAll(@Param("key") String key);

    /**
     * 根据ID查询城市信息
     *
     * @param id 城市ID
     * @return 城市信息
     */
    City selectById(@Param("id") Integer id);

    /**
     * 根据城市名称查询城市信息
     *
     * @param cityName 城市名称
     * @return 城市信息
     */
    City selectByName(@Param("cityName") String cityName);

    /**
     * 添加城市
     *
     * @param city 城市信息
     * @return 影响行数
     */
    int insert(City city);

    /**
     * 更新城市信息
     *
     * @param city 城市信息
     * @return 影响行数
     */
    int update(City city);

    /**
     * 根据ID删除城市
     *
     * @param id 城市ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 批量删除城市
     *
     * @param ids 城市ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Integer> ids);

    /**
     * 检查城市下是否有单位
     *
     * @param cityId 城市ID
     * @return 单位数量
     */
    int countOrganizationsByCityId(@Param("cityId") Integer cityId);
}

