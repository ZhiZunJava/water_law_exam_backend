package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷组实体，对应表 tb_papers_group
 */
@Data
public class PapersGroup {
    private Long id;
    private String groupTitle;
    private Integer papersCount;
    private Integer totalScore;
    private Integer templateId;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}









































