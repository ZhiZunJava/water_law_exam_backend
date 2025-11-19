package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PapersContent {
    private Long id;
    private Long papersId;
    private Integer papersNo;
    private Long itemId;
    private Integer typeId;
    private BigDecimal score;
    private Integer sortOrder;
}
