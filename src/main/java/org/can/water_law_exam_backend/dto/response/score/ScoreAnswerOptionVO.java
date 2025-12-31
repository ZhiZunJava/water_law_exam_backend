package org.can.water_law_exam_backend.dto.response.score;

import lombok.Data;

@Data
public class ScoreAnswerOptionVO {
    private Integer no;
    private String title;
    private Boolean correct;
    private Boolean chosen;
}


