package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.TestStruct;

import java.util.List;

@Mapper
public interface TestStructMapper {

    List<TestStruct> selectAllWithType();

    int deleteAll();

    int insertBatch(@Param("list") List<TestStruct> list);
}


