package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.TemplateDetail;

import java.util.List;

@Mapper
public interface TemplateDetailMapper {
    int deleteByTemplateId(@Param("templateId") Integer templateId);
    int insertBatch(@Param("list") List<TemplateDetail> list);
    List<TemplateDetail> selectByTemplateId(@Param("templateId") Integer templateId);
}
