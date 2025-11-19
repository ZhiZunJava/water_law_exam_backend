package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 组卷模板
 */
@Data
public class PapersTemplate {
    private Integer id;
    private String templateName;
    private Integer papersCount;
    private Integer maxScore;
    private Integer keyProportion;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
