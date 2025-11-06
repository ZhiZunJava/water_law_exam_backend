package org.can.water_law_exam_backend.dto.response.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应DTO
 *
 * @author 程安宁
 * @date 2025/11/06
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

