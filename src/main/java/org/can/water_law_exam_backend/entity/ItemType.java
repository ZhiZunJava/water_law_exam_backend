package org.can.water_law_exam_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 题型实体类
 *
 * @author 程安宁
 * @date 2025/10/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemType {
    /**
     * 题型ID
     */
    private Integer id;

    /**
     * 题型名称
     */
    private String typeName;

    /**
     * 题型说明
     */
    private String typeRemarks;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

