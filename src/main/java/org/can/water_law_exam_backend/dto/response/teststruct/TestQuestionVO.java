package org.can.water_law_exam_backend.dto.response.teststruct;

import lombok.Data;

import java.util.List;

@Data
public class TestQuestionVO {
    private Long questionId; // 题目ID
    private Integer typeId; // 题型ID
    private String typeName; // 题型名称
    private String content; // 题目内容
    private String explanation; // 题目解析
    private List<TestOptionVO> optionList; // 选项列表
}