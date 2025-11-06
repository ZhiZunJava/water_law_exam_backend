package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 *
 * @author 程安宁
 * @date 2025/11/06
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



