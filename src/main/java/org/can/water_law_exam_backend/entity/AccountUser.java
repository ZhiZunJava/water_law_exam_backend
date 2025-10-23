package org.can.water_law_exam_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学员实体类
 */
@Data
public class AccountUser {

    /**
     * 学员ID
     */
    private Long id;

    /**
     * 学员姓名
     */
    private String name;

    /**
     * 所属单位ID
     */
    private Long orgId;

    /**
     * 身份证号
     */
    private String idNo;

    /**
     * 手机号
     */
    private String phone;

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


