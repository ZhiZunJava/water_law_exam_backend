package org.can.water_law_exam_backend.dto.request.teststruct;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TestStructSetRequest {
    @NotNull(message = "题目类型不能为空")
    private Integer typeId;

    @NotNull(message = "题目分数不能为空")
    @Min(value = 0, message = "题目分数不能小于0")
    private Integer score;

    private String remarks;

    @NotNull(message = "题型总题数不能为空")
    @Min(value = 0, message = "题型总题数不能小于0")
    private Integer totality;
}


