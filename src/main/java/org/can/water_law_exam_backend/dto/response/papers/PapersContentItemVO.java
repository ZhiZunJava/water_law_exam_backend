package org.can.water_law_exam_backend.dto.response.papers;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PapersContentItemVO {
    private Long id;
    private BigDecimal score;
    private String typeName;
    private String content;
    private List<PapersContentOptionVO> options;
}
