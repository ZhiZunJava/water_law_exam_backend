package org.can.water_law_exam_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性类
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥
     */
    private String secret;

    /**
     * Token 过期时间（毫秒）
     */
    private Long expiration;

    /**
     * HTTP 请求头名称
     */
    private String header;

    /**
     * Token 前缀
     */
    private String tokenPrefix;
}



