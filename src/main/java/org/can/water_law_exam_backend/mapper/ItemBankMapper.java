package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.ItemBank;

import java.util.List;

/**
 * 题库Mapper接口
 */
@Mapper
public interface ItemBankMapper {

    /**
     * 插入题目
     *
     * @param itemBank 题目信息
     * @return 影响行数
     */
    int insert(ItemBank itemBank);

    /**
     * 更新题目
     *
     * @param itemBank 题目信息
     * @return 影响行数
     */
    int update(ItemBank itemBank);

    /**
     * 根据ID删除题目
     *
     * @param id 题目ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除题目
     *
     * @param ids 题目ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 根据ID查询题目（包含题型和分类名称）
     *
     * @param id 题目ID
     * @return 题目信息
     */
    ItemBank selectById(@Param("id") Long id);

    /**
     * 分页查询题目列表（使用PageHelper）
     *
     * @param categoryId 分类ID（可选）
     * @param typeId     题型ID（可选）
     * @param key        题目关键字（可选）
     * @return 题目列表
     */
    List<ItemBank> selectByPage(@Param("categoryId") Integer categoryId,
                                 @Param("typeId") Integer typeId,
                                 @Param("key") String key);
}

