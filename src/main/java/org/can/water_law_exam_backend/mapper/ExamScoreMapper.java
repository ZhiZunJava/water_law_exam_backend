package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.ExamScore;

import java.util.List;

@Mapper
public interface ExamScoreMapper {

    ExamScore selectByBatchAndUser(@Param("batchId") Long batchId,
                                   @Param("userId") Long userId);

    int insert(ExamScore entity);

    int update(ExamScore entity);

    List<ExamScore> selectByBatchAndFilter(@Param("batchId") Long batchId,
                                           @Param("key") String key,
                                           @Param("c") Integer category);

    List<ExamScore> selectPassByBatch(@Param("batchId") Long batchId);
}


