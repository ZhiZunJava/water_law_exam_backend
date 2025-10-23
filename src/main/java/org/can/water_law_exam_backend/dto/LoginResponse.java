package org.can.water_law_exam_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 姓名
     */
    private String name;

    /**
     * 用户类型（admin/user）
     */
    private String userType;

    /**
     * Token前缀
     */
    private String tokenPrefix;

    /**
     * 过期时间（毫秒）
     */
    private Long expiration;
}



