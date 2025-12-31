package org.can.water_law_exam_backend.dto.response.score;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ScoreAnswerItemVO {
    private Long id;                  // 题目ID
    private BigDecimal score;         // 该题分值
    private String typeName;          // 题型名称
    private String content;           // 题干
    private List<ScoreAnswerOptionVO> options; // 选项
    private List<Integer> userAnswer; // 用户选择的选项序号集合
    private List<Integer> correctAnswer; // 正确选项序号集合
    private Boolean isCorrect;        // 是否答对
}






































