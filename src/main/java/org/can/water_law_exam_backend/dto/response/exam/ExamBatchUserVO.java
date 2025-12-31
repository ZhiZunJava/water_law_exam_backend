package org.can.water_law_exam_backend.dto.response.exam;

import lombok.Data;

@Data
public class ExamBatchUserVO {
    private Long id;
    private String batchName;
    private String startTime;
    private String endTime;
    private Integer lateMinutes;
    private Integer advanceMinutes;
    private Boolean joined;
    private Boolean started;
    private Boolean submitted;
}



