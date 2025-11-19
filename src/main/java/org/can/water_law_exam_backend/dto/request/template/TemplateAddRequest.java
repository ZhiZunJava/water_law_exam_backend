package org.can.water_law_exam_backend.dto.request.template;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TemplateAddRequest {
    @NotBlank
    private String templateName;
    @NotNull @Min(1)
    private Integer papersCount;
    @NotNull @Min(1)
    private Integer maxScore;
    @NotNull @Min(0) @Max(100)
    private Integer keyProportion;
    @NotNull
    private List<DetailDTO> details;

    @Data
    public static class DetailDTO {
        @NotNull
        private Integer typeId;
        private String remarks; // 不入库，来自题型说明
        @NotNull @Min(1)
        private Integer totality; // 题量
        @NotNull @Min(1)
        private Integer totalScore; // 该题型总分
    }
}
