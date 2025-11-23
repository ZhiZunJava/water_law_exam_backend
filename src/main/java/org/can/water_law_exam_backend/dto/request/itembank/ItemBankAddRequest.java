package org.can.water_law_exam_backend.dto.request.itembank;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 添加题目请求DTO
 */
@Data
public class ItemBankAddRequest {

    /**
     * 题型ID
     */
    @NotNull(message = "题型ID不能为空")
    private Integer typeId;

    /**
     * 题干内容
     */
    @NotBlank(message = "题干内容不能为空")
    private String content;

    /**
     * 题目答案解析
     */
    private String explain;

    /**
     * 题目分类ID
     */
    @NotNull(message = "题目分类ID不能为空")
    private Integer categoryId;

    @NotNull(message = "题目是否重点不能为空")
    private Boolean isKeyItem;

    /**
     * 答案选项列表
     */
    @NotEmpty(message = "答案选项不能为空")
    @Valid
    private List<OptionDTO> options;

    /**
     * 选项DTO
     * 注意：判断题仅传 [{"checked": false}] 或 [{"checked": true}]
     * 单选多选题需要传 optionTitle 和 checked
     */
    @Data
    public static class OptionDTO {
        /**
         * 选项序号（可选，后端会自动生成）
         */
        private Integer optionNo;

        /**
         * 选项内容（单选/多选必填，判断题不需要）
         */
        private String title;

        /**
         * 是否为正确答案
         */
        @NotNull(message = "是否为正确答案不能为空")
        private Boolean checked;
    }
}

