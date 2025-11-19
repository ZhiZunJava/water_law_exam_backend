package org.can.water_law_exam_backend.dto.response.template;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TemplateDetailVO {
    private Integer typeId;
    private String remarks;
    private Integer totality; // itemCount
    private BigDecimal scorePerItem;
    private Integer totalScore; // 仅用于响应展示（totality * scorePerItem）
}
