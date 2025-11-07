package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.ItemOption;

import java.util.List;

/**
 * 题目选项Mapper接口
 */
@Mapper
public interface ItemOptionMapper {

    /**
     * 插入选项
     *
     * @param itemOption 选项信息
     * @return 影响行数
     */
    int insert(ItemOption itemOption);

    /**
     * 批量插入选项
     *
     * @param options 选项列表
     * @return 影响行数
     */
    int insertBatch(@Param("options") List<ItemOption> options);

    /**
     * 根据题目ID删除所有选项
     *
     * @param itemId 题目ID
     * @return 影响行数
     */
    int deleteByItemId(@Param("itemId") Long itemId);

    /**
     * 根据题目ID查询所有选项
     *
     * @param itemId 题目ID
     * @return 选项列表
     */
    List<ItemOption> selectByItemId(@Param("itemId") Long itemId);
}

