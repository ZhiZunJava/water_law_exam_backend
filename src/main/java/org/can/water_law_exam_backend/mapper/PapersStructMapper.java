package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.PapersStruct;

import java.util.List;

@Mapper
public interface PapersStructMapper {
    int insertBatch(@Param("list") List<PapersStruct> list);
    int deleteByPapersIds(@Param("ids") List<Long> ids);
    List<PapersStruct> selectByPapersId(@Param("papersId") Long papersId);
}
