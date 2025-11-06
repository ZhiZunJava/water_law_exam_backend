package org.can.water_law_exam_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.config.JwtProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token管理服务
 * 使用Redis存储token，实现token的撤销、强制下线等功能
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Slf4j
@Service
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProperties jwtProperties;

    private static final String TOKEN_KEY_PREFIX = "token:";
    private static final String USER_TOKEN_KEY_PREFIX = "user:token:";

    public TokenService(RedisTemplate<String, Object> redisTemplate, JwtProperties jwtProperties) {
        this.redisTemplate = redisTemplate;
        this.jwtProperties = jwtProperties;
    }

    /**
     * 存储token到Redis
     *
     * @param token  token字符串
     * @param userId 用户ID
     */
    public void saveToken(String token, Long userId) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        String userTokenKey = USER_TOKEN_KEY_PREFIX + userId;

        // 存储token -> userId的映射
        redisTemplate.opsForValue().set(
                tokenKey,
                userId,
                jwtProperties.getExpiration(),
                TimeUnit.MILLISECONDS
        );

        // 存储userId -> token的映射（用于强制下线）
        redisTemplate.opsForValue().set(
                userTokenKey,
                token,
                jwtProperties.getExpiration(),
                TimeUnit.MILLISECONDS
        );

        log.debug("Token已存储到Redis：userId={}, token前缀={}", userId, token.substring(0, Math.min(20, token.length())));
    }

    /**
     * 验证token是否存在于Redis中
     *
     * @param token token字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(tokenKey);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 从Redis中删除token（登出）
     *
     * @param token token字符串
     */
    public void removeToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        
        // 先获取userId
        Object userId = redisTemplate.opsForValue().get(tokenKey);
        
        // 删除token -> userId映射
        redisTemplate.delete(tokenKey);
        
        // 删除userId -> token映射
        if (userId != null) {
            String userTokenKey = USER_TOKEN_KEY_PREFIX + userId;
            redisTemplate.delete(userTokenKey);
            log.info("Token已删除：userId={}", userId);
        }
    }

    /**
     * 强制用户下线（删除用户的所有token）
     *
     * @param userId 用户ID
     */
    public void forceLogout(Long userId) {
        String userTokenKey = USER_TOKEN_KEY_PREFIX + userId;
        
        // 获取用户的token
        Object token = redisTemplate.opsForValue().get(userTokenKey);
        
        if (token != null) {
            // 删除token -> userId映射
            String tokenKey = TOKEN_KEY_PREFIX + token;
            redisTemplate.delete(tokenKey);
            
            // 删除userId -> token映射
            redisTemplate.delete(userTokenKey);
            
            log.info("用户已被强制下线：userId={}", userId);
        }
    }

    /**
     * 刷新token的过期时间
     *
     * @param token token字符串
     */
    public void refreshTokenExpiration(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        
        // 获取userId
        Object userId = redisTemplate.opsForValue().get(tokenKey);
        
        if (userId != null) {
            // 刷新token -> userId映射的过期时间
            redisTemplate.expire(tokenKey, jwtProperties.getExpiration(), TimeUnit.MILLISECONDS);
            
            // 刷新userId -> token映射的过期时间
            String userTokenKey = USER_TOKEN_KEY_PREFIX + userId;
            redisTemplate.expire(userTokenKey, jwtProperties.getExpiration(), TimeUnit.MILLISECONDS);
            
            log.debug("Token过期时间已刷新：userId={}", userId);
        }
    }

    /**
     * 获取token对应的用户ID
     *
     * @param token token字符串
     * @return 用户ID，如果token不存在则返回null
     */
    public Long getUserIdByToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        Object userId = redisTemplate.opsForValue().get(tokenKey);
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }
}


