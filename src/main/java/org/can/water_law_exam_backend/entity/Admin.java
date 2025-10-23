package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员实体类
 */
@Data
public class Admin {

    /**
     * 管理员ID
     */
    private Long id;

    /**
     * 登录账号
     */
    private String userNo;

    /**
     * 管理员姓名
     */
    private String name;

    /**
     * 登录密码（加密存储）
     */
    private String pwd;

    /**
     * 是否禁用：false-正常，true-禁用
     */
    private Boolean locked;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}



