package org.can.water_law_exam_backend.dto.response.papers;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PapersContentVO {
    private Long id;
    private Integer no;
    private String title;
    private Integer totalScore;
    private List<PapersStructVO> structs;
    private Map<String, List<PapersContentItemVO>> content; // key: 题型中文名
}
