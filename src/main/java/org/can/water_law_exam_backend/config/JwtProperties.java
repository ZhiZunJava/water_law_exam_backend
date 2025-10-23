package org.can.water_law_exam_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性类
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT签名密钥
     */
    private String secret;

    /**
     * Token过期时间（毫秒）
     */
    private Long expiration;

    /**
     * HTTP请求头名称
     */
    private String header;

    /**
     * Token前缀
     */
    private String tokenPrefix;
}



