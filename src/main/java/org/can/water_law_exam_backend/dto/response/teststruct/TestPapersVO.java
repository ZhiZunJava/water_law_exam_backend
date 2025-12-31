package org.can.water_law_exam_backend.dto.response.teststruct;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TestPapersVO {
    private int totalScore;
    // 3种题型数量
    private int singleChoiceCount; // 单选题数量
    private int multipleChoiceCount; // 多选题数量
    private int judgmentCount; // 判断题数量

    // 题型结构列表（原有TestStructVO）
    private List<TestStructVO> testStructList;

    // 题目内容（key=题型名称，value=该题型下所有题目）
    private Map<String, List<TestQuestionVO>> questionMap;
}
