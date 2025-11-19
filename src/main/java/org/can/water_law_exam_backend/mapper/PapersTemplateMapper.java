package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.PapersTemplate;

import java.util.List;

@Mapper
public interface PapersTemplateMapper {
    int insert(PapersTemplate entity);
    int update(PapersTemplate entity);
    int deleteBatch(@Param("ids") List<Integer> ids);
    PapersTemplate selectById(@Param("id") Integer id);
    PapersTemplate selectByTemplateName(@Param("templateName")String templateName);
    List<PapersTemplate> selectByPage(@Param("key") String key);
}
