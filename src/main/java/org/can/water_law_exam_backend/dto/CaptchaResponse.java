package org.can.water_law_exam_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponse {
    
    /**
     * 验证码唯一标识（UUID）
     */
    private String captchaId;
    
    /**
     * 验证码图片Base64编码
     */
    private String captchaImage;
    
    /**
     * 过期时间（秒）
     */
    private Long expireTime;
}

