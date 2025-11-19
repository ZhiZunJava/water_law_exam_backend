package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.ItemType;

import java.util.List;

/**
 * 题型Mapper接口
 *
 * @author 程安宁
 * @date 2025/10/28
 */
@Mapper
public interface ItemTypeMapper {
    
    /**
     * 查询所有题型
     *
     * @return 题型列表
     */
    List<ItemType> selectAll();

    /**
     * 根据ID查询题型
     *
     * @param id 题型ID
     * @return 题型信息
     */
    ItemType selectById(@Param("id") Integer id);

    /**
     * 新增题型
     *
     * @param itemType 题型信息
     * @return 影响行数
     */
    //int insert(ItemType itemType);

    /**
     * 更新题型
     *
     * @param itemType 题型信息
     * @return 影响行数
     */
    //int update(ItemType itemType);

    /**
     * 删除题型
     *
     * @param id 题型ID
     * @return 影响行数
     */
    //int deleteById(Integer id);
}

