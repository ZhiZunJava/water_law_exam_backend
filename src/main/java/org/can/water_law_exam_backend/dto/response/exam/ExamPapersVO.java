package org.can.water_law_exam_backend.dto.response.exam;

import lombok.Data;
import org.can.water_law_exam_backend.dto.response.papers.PapersContentItemVO;
import org.can.water_law_exam_backend.dto.response.papers.PapersStructVO;

import java.util.List;
import java.util.Map;

@Data
public class ExamPapersVO {
    private Long batchId;
    private String title;
    private Integer totalScore;
    private String startTime;
    private String endTime;
    private Integer prepareMinutes;
    private List<PapersStructVO> structs;
    private Map<String, List<PapersContentItemVO>> content;
}



