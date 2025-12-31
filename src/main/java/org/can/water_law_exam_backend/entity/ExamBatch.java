package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考试批次实体，对应表 tb_exam_batch
 */
@Data
public class ExamBatch {
    private Long id;
    private String batchName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer prepareMinutes;
    private Integer advanceMinutes;
    private Integer lateMinutes;
    private Boolean optionsRandom;
    private Boolean itemRandom;
    private Long papersId;
    private Boolean selfJoin;
    private Boolean reviewRequired;
    private Boolean released;
    private Boolean papersDistributed;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}



