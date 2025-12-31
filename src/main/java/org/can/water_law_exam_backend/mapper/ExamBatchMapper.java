package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.ExamBatch;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ExamBatchMapper {

    int insert(ExamBatch entity);

    int update(ExamBatch entity);

    int deleteBatch(@Param("ids") List<Long> ids);

    ExamBatch selectById(@Param("id") Long id);

    /**
     * 查询已启用但未开始的考试批次（已启用=已发布且试卷已分发，且当前时间 < end_time）
     */
    List<ExamBatch> selectEnabledNotStarted(@Param("now") LocalDateTime now);

    /**
     * 分页查询全部批次，支持按名称关键字与状态检索
     *
     * @param key  名称关键字
     * @param lock 状态：null=全部，true=未启用，false=启用
     */
    List<ExamBatch> selectByPage(@Param("key") String key,
                                 @Param("lock") Boolean lock);

    /**
     * 查询可报名的考试批次：
     * 已发布、未分发试卷、考试未开始
     */
    List<ExamBatch> selectJoinable(@Param("now") LocalDateTime now);
}


