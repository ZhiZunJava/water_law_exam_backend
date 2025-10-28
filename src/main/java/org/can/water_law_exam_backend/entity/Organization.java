package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单位实体类
 */
@Data
public class Organization {

    /**
     * 单位ID
     */
    private Long id;

    /**
     * 单位名称
     */
    private String orgName;

    /**
     * 所属城市ID
     */
    private Integer cityId;

    /**
     * 所属城市名称（非数据库字段，仅用于查询结果映射）
     */
    private transient String cityName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

