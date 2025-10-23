package org.can.water_law_exam_backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.dto.response.captcha.CaptchaResponse;
import org.can.water_law_exam_backend.service.CaptchaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码控制器
 */
@Slf4j
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 获取验证码
     *
     * @return 验证码响应
     */
    @GetMapping
    public Result<CaptchaResponse> getCaptcha() {
        log.info("获取验证码请求");
        CaptchaResponse response = captchaService.generateCaptcha();
        return Result.success("获取验证码成功", response);
    }
}

