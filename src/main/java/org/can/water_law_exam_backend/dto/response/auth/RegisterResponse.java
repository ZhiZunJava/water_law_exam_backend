package org.can.water_law_exam_backend.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户注册响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号（登录账号）
     */
    private String idNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 所属单位ID
     */
    private Long orgId;

    /**
     * 所属单位名称
     */
    private String orgName;

    /**
     * 注册时间
     */
    private Long registerTime;

    /**
     * 提示信息
     */
    private String message;
}

