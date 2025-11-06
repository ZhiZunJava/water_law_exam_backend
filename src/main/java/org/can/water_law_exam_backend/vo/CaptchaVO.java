package org.can.water_law_exam_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码视图对象
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {
    
    /**
     * 验证码文本
     */
    private String code;
    
    /**
     * 验证码图片Base64编码
     */
    private String imageBase64;
}

