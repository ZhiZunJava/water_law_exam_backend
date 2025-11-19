package org.can.water_law_exam_backend.dto.response.template;

import lombok.Data;

import java.util.List;

@Data
public class TemplateVO {
    private Integer id;
    private String templateName;
    private Integer papersCount;
    private Integer maxScore;
    private Integer keyProportion;
    private List<TemplateDetailVO> details;
}
