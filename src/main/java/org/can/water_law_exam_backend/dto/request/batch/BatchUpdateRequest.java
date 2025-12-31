package org.can.water_law_exam_backend.dto.request.batch;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BatchUpdateRequest {

    @NotNull
    @Min(1)
    private Long id;

    @NotBlank
    private String batchName;

    @NotBlank
    private String startTime; // yyyy-MM-dd HH:mm:ss

    @NotBlank
    private String endTime;   // yyyy-MM-dd HH:mm:ss

    @NotNull
    @Min(0)
    private Integer prepareMinutes;

    @NotNull
    @Min(0)
    private Integer advanceMinutes;

    @NotNull
    @Min(0)
    private Integer lateMinutes;

    @NotNull
    private Boolean optionsRandom;

    @NotNull
    private Boolean itemRandom;

    @NotNull
    @Min(1)
    private Long papersId;

    @NotNull
    private Boolean selfJoin;

    @NotNull
    private Boolean reviewRequired;
}



