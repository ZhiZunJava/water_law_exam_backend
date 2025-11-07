package org.can.water_law_exam_backend.entity;

import lombok.Data;

/**
 * 题目选项实体类
 */
@Data
public class ItemOption {

    /**
     * 选项ID
     */
    private Long id;

    /**
     * 题目ID
     */
    private Long itemId;

    /**
     * 选项序号
     */
    private Integer optionNo;

    /**
     * 选项内容
     */
    private String optionTitle;

    /**
     * 是否为正确答案
     */
    private Boolean isCorrect;
}

