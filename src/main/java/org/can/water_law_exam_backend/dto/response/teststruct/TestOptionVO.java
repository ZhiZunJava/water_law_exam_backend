package org.can.water_law_exam_backend.dto.response.teststruct;

import lombok.Data;

@Data
public class TestOptionVO {
    private String optionTitle; // 选项内容（如：A. 中华人民共和国水法）
    private Boolean isCorrect; // 是否正确选项（true=正确，false=错误，null=未知）
}