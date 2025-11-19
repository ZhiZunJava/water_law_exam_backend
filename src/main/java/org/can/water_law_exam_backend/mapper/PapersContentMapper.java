package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.PapersContent;

import java.util.List;

@Mapper
public interface PapersContentMapper {
    int insertBatch(@Param("list") List<PapersContent> list);
    int deleteByPapersIds(@Param("ids") List<Long> ids);
    List<PapersContent> selectByPapersIdAndNo(@Param("papersId") Long papersId, @Param("papersNo") Integer papersNo);
}
