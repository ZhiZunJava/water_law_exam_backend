package org.can.water_law_exam_backend.dto.response.batch;

import lombok.Data;

@Data
public class BatchVO {
    private Long id;
    private String batchName;
    private Long papersId;
    private String startTime;
    private String endTime;
    private Integer lateMinutes;
    private Integer prepareMinutes;
    private Integer advanceMinutes;
    private Boolean optionsRandom;
    private Boolean itemRandom;
    private Boolean status;          // 是否启用（已发布且已分发）
    private Boolean released;        // 是否已发布
    private Boolean selfJoin;
    private Boolean reviewRequired;
}



