package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考试答题记录实体，对应表 tb_exam_answer
 */
@Data
public class ExamAnswer {
    private Long id;
    private Long batchId;
    private Long userId;
    private Long itemId;
    /**
     * 答案内容（JSON数组，存储选项序号）
     */
    private String answerContent;
    private Boolean isCorrect;
    private Double score;
    private LocalDateTime updateTime;
}



