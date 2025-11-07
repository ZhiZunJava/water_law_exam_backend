package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题库实体类
 */
@Data
public class ItemBank {

    /**
     * 题目ID
     */
    private Long id;

    /**
     * 题型ID
     */
    private Integer typeId;

    /**
     * 题型名称（非数据库字段）
     */
    private transient String typeName;

    /**
     * 分类ID（对应法律规章）
     */
    private Integer categoryId;

    /**
     * 分类名称（非数据库字段）
     */
    private transient String categoryName;

    /**
     * 题干内容
     */
    private String content;

    /**
     * 答案解析
     */
    private String explanation;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 选项列表（非数据库字段，用于返回完整题目信息）
     */
    private transient List<ItemOption> options;
}

