package org.can.water_law_exam_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置类
 * 独立配置以避免循环依赖
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 密码编码器
     *
     * @return {@link PasswordEncoder }
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


