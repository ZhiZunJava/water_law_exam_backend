package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考生实体，对应表 tb_examinee
 */
@Data
public class Examinee {
    private Long id;
    private Long batchId;
    private Long userId;
    private Integer papersNo;
    /**
     * 审核状态：0-未审核；1-审核通过；-1-审核不通过
     */
    private Integer reviewStatus;
    private Boolean examStarted;
    private LocalDateTime examStartTime;
    private Boolean submitted;
    private LocalDateTime submitTime;
    private LocalDateTime createTime;
}



