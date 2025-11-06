package org.can.water_law_exam_backend.service;

import cn.hutool.core.lang.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.dto.response.captcha.CaptchaResponse;
import org.can.water_law_exam_backend.util.CaptchaUtil;
import org.can.water_law_exam_backend.vo.CaptchaVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 验证码Redis键前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    /**
     * 验证码过期时间（秒）
     */
    private static final long CAPTCHA_EXPIRE_TIME = 120;

    /**
     * 生成验证码
     *
     * @return 验证码响应
     */
    public CaptchaResponse generateCaptcha() {
        // 生成验证码
        CaptchaVO result = CaptchaUtil.generate();
        
        // 生成唯一标识
        String captchaId = UUID.randomUUID().toString(true);
        
        // 存储到Redis（不区分大小写，统一转为小写存储）
        String redisKey = CAPTCHA_KEY_PREFIX + captchaId;
        redisTemplate.opsForValue().set(
            redisKey,
            result.getCode().toLowerCase(),
            CAPTCHA_EXPIRE_TIME,
            TimeUnit.SECONDS
        );
        
        log.debug("生成验证码：ID={}, Code={}", captchaId, result.getCode());
        
        return new CaptchaResponse(captchaId, result.getImageBase64(), CAPTCHA_EXPIRE_TIME);
    }

    /**
     * 验证验证码
     *
     * @param captchaId   验证码ID
     * @param captchaCode 用户输入的验证码
     * @return 验证结果
     */
    public boolean verifyCaptcha(String captchaId, String captchaCode) {
        // 检查参数是否为空
        if (captchaId == null || captchaId.trim().isEmpty()) {
            log.warn("验证码验证失败：验证码ID为空");
            return false;
        }
        
        if (captchaCode == null || captchaCode.trim().isEmpty()) {
            log.warn("验证码验证失败：验证码为空，ID={}", captchaId);
            return false;
        }

        String redisKey = CAPTCHA_KEY_PREFIX + captchaId;
        String storedCode = redisTemplate.opsForValue().get(redisKey);
        
        if (storedCode == null) {
            log.warn("验证码验证失败：验证码不存在或已过期，ID={}", captchaId);
            return false;
        }

        // 不区分大小写比较
        boolean matched = storedCode.equalsIgnoreCase(captchaCode.trim());
        
        // 验证成功后立即删除，防止重复使用
        if (matched) {
            redisTemplate.delete(redisKey);
            log.debug("验证码验证成功：ID={}", captchaId);
        } else {
            // 验证失败也删除，防止暴力破解
            redisTemplate.delete(redisKey);
            log.warn("验证码验证失败：验证码错误，ID={}, 期望={}, 实际={}", 
                captchaId, storedCode, captchaCode.trim());
        }
        
        return matched;
    }

    /**
     * 清除验证码（用于测试或特殊情况）
     *
     * @param captchaId 验证码ID
     */
    public void removeCaptcha(String captchaId) {
        if (captchaId != null) {
            String redisKey = CAPTCHA_KEY_PREFIX + captchaId;
            redisTemplate.delete(redisKey);
            log.debug("清除验证码：ID={}", captchaId);
        }
    }
}

