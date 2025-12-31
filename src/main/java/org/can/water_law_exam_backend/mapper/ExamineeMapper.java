package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.Examinee;

import java.util.List;

@Mapper
public interface ExamineeMapper {

    int insertBatch(@Param("list") List<Examinee> list);

    int deleteByBatchAndUserIds(@Param("batchId") Long batchId,
                                @Param("userIds") List<Long> userIds);

    /**
     * 分页查询指定批次的考生（已绑定）
     */
    List<Examinee> selectByBatch(@Param("batchId") Long batchId,
                                 @Param("key") String key,
                                 @Param("status") Integer status);

    /**
     * 查询指定用户的所有报名记录
     */
    List<Examinee> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询指定批次+用户的报名记录
     */
    List<Examinee> selectByUserAndBatch(@Param("userId") Long userId,
                                        @Param("batchId") Long batchId);

    /**
     * 查询指定用户所有未提交的考试记录
     */
    List<Examinee> selectUnsubmittedByUser(@Param("userId") Long userId);

    /**
     * 分页查询可添加到指定批次的考生（未绑定该批次）
     */
    List<Long> selectOptionalUserIds(@Param("batchId") Long batchId,
                                     @Param("key") String key);

    int updateReviewStatus(@Param("batchId") Long batchId,
                           @Param("ids") List<Long> userIds,
                           @Param("status") Integer status);
}


