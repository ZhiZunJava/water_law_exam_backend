package org.can.water_law_exam_backend.dto.response.auth;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 当前登录用户详细信息响应
 */
@Data
@Builder
public class CurrentUserResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名/账号
     */
    private String username;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户类型：ADMIN-管理员，USER-学员
     */
    private String userType;

    /**
     * 身份证号（仅学员）
     */
    private String idNo;

    /**
     * 手机号（仅学员）
     */
    private String phone;

    /**
     * 所属单位ID（仅学员）
     */
    private Long orgId;

    /**
     * 所属单位名称（仅学员）
     */
    private String orgName;

    /**
     * 登录账号（仅管理员）
     */
    private String userNo;

    /**
     * 账号状态：false-正常，true-禁用
     */
    private Boolean locked;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;

    /**
     * Token信息
     */
    private String token;

    /**
     * Token过期时间（毫秒时间戳）
     */
    private Long tokenExpireAt;
}

