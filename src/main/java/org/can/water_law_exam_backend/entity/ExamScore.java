package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 成绩实体，对应表 tb_exam_score
 */
@Data
public class ExamScore {
    private Long id;
    private Long batchId;
    private Long userId;
    private Double totalScore;
    private Double passScore;
    private Boolean isPass;
    private Integer examDuration;
    private LocalDateTime submitTime;
    private LocalDateTime createTime;
}



