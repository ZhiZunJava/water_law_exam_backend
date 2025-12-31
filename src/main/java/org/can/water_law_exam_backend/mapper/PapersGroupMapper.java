package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.PapersGroup;

import java.util.List;

@Mapper
public interface PapersGroupMapper {

    int insert(PapersGroup entity);

    int deleteBatch(@Param("ids") List<Long> ids);

    List<PapersGroup> selectByPage(@Param("key") String key);

    PapersGroup selectById(@Param("id") Long id);
}









































