package org.can.water_law_exam_backend.dto.response.papers;

import lombok.Data;

@Data
public class PapersAbstractVO {
    private Long id;
    private String title;
    private Integer papersCount;
    private Integer score;
    private String createTime;
}
