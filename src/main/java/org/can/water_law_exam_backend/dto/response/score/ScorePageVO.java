package org.can.water_law_exam_backend.dto.response.score;

import lombok.Data;

@Data
public class ScorePageVO {
    private Long userId;
    private String userName;
    private String org;
    private String idNo;
    private String phone;
    private Double score;
    /**
     * 是否已提交试卷
     */
    private Boolean submitted;
}



