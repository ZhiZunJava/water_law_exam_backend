package org.can.water_law_exam_backend.entity;

import lombok.Data;

@Data
public class PapersStruct {
    private Long id;
    private Long papersId;
    private Integer typeId;
    private String typeName;
    private String typeRemarks;
    private Integer score;
}
