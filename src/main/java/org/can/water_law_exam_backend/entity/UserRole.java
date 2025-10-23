package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 */
@Data
public class UserRole {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 学员ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}


