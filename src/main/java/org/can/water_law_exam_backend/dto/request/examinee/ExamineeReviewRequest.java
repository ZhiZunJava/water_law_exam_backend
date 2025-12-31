package org.can.water_law_exam_backend.dto.request.examinee;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ExamineeReviewRequest {

    @NotNull
    private Long batchId;

    /**
     * 审核结论：true-审核通过；false-审核不通过
     */
    @NotNull
    private Boolean rs;

    /**
     * 待审核考生ID集合（userId）
     */
    @NotEmpty
    private List<Long> ids;
}



