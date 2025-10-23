package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 城市实体类
 */
@Data
public class City {

    /**
     * 城市ID
     */
    private Integer id;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

