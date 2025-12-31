package org.can.water_law_exam_backend.dto.response.score;

import lombok.Data;
import org.can.water_law_exam_backend.dto.response.papers.PapersStructVO;

import java.util.List;
import java.util.Map;

@Data
public class ScoreDetailVO {
    private Long batchId;
    private String title;
    private Integer totalScore;
    private String startTime;
    private String endTime;
    private Integer prepareMinutes;
    /**
     * 试卷结构信息列表
     */
    private List<PapersStructVO> structs;

    /**
     * 按题型分组的答题明细
     */
    private Map<String, List<ScoreAnswerItemVO>> content;
}



