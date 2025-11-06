package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体类
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Data
public class Role {

    /**
     * 角色ID
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String roleDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}