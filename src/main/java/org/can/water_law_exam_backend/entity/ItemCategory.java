package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目分类实体
 */
@Data
public class ItemCategory {

    /**
     * 分类ID
     */
    private Integer id;

    /**
     * 分类标题
     */
    private String title;

    /**
     * 父分类ID，0 表示顶级
     */
    private Integer parentId;

    /**
     * 是否为叶子节点
     */
    private Boolean isLeaf;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}


