package org.can.water_law_exam_backend.dto.request.exam;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AnswerRequest {

    /**
     * 试题ID（题目ID）
     */
    @NotNull
    private Long id;

    /**
     * 考生答案：
     * 选择题：选项序号数组（如[1,4]）
     * 判断题：[1=正确，0=错误]
     */
    @NotEmpty
    private List<Integer> ans;
}



