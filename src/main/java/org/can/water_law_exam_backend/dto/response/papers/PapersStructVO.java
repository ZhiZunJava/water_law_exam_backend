package org.can.water_law_exam_backend.dto.response.papers;

import lombok.Data;

@Data
public class PapersStructVO {
    private Long papersId;
    private String typeName;
    private String typeRemarks;
    private Integer score;
}
