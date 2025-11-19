package org.can.water_law_exam_backend.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 组卷模板明细
 */
@Data
public class TemplateDetail {
    private Long id;
    private Integer templateId;
    private Integer typeId;
    private Integer itemCount; // totality
    private BigDecimal scorePerItem;
}
