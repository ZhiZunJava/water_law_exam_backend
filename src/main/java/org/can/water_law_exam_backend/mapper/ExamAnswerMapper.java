package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.ExamAnswer;

import java.util.List;

@Mapper
public interface ExamAnswerMapper {

    int insert(ExamAnswer entity);

    int deleteByBatchUserAndItem(@Param("batchId") Long batchId,
                                 @Param("userId") Long userId,
                                 @Param("itemId") Long itemId);

    List<ExamAnswer> selectByBatchAndUser(@Param("batchId") Long batchId,
                                          @Param("userId") Long userId);

    ExamAnswer selectById(@Param("id") Long id);

    int update(ExamAnswer entity);
}


