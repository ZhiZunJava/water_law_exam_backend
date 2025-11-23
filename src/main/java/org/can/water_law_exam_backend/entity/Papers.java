package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Papers {
    private Long id;
    private String papersTitle;
    private Integer papersNo;
    private Integer totalScore;
    private Integer templateId;
    private Long creatorId;
    private LocalDateTime createTime;
}
