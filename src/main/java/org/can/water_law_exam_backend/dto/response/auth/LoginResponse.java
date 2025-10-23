package org.can.water_law_exam_backend.dto.response.auth;

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
     * JWT Token
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
     * 用户类型
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

