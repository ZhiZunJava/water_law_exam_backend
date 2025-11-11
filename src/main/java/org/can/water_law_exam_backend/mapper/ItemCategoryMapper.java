package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.ItemCategory;

import java.util.List;

@Mapper
public interface ItemCategoryMapper {

    List<ItemCategory> selectAll();

    ItemCategory selectById(@Param("id") Integer id);

    List<ItemCategory> selectByParentId(@Param("parentId") Integer parentId);

    int countChildren(@Param("id") Integer id);

    int countItemsByCategoryId(@Param("categoryId") Integer categoryId);

    int insert(ItemCategory category);

    int update(ItemCategory category);

    int deleteById(@Param("id") Integer id);

    int deleteBatch(@Param("ids") List<Integer> ids);
}


