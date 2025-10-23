package org.can.water_law_exam_backend;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密测试工具
 * 用于测试BCrypt密码加密和验证
 */
public class PasswordTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 测试密码加密
     * 运行此测试可以生成BCrypt加密后的密码
     */
    @Test
    public void testEncodePassword() {
        // 原始密码
        String rawPassword = "123456";
        
        // 加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
        System.out.println("密码长度: " + encodedPassword.length());
    }

    /**
     * 测试密码验证
     * 用于验证原始密码与数据库中的加密密码是否匹配
     */
    @Test
    public void testPasswordMatch() {
        // 原始密码
        String rawPassword = "123456";
        
        // 数据库中的加密密码（替换为你数据库中实际的密码）
        String encodedPassword = "$2a$10$N.zmdr9k7uOCQb3Xwqy4/..zWC8BaZAeEnQbYPR7bvk2vJHCYyNbG";
        
        // 验证密码
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("数据库密码: " + encodedPassword);
        System.out.println("密码匹配: " + matches);
    }

    /**
     * 生成多个常用密码的BCrypt哈希值
     */
    @Test
    public void generateCommonPasswords() {
        String[] passwords = {"123456", "admin123", "password", "admin", "000000"};
        
        System.out.println("=== 常用密码BCrypt哈希值 ===");
        for (String password : passwords) {
            String encoded = passwordEncoder.encode(password);
            System.out.println("密码: " + password + " -> " + encoded);
        }
    }

    /**
     * 测试数据库密码格式是否正确
     */
    @Test
    public void testDatabasePasswordFormat() {
        // 替换为你数据库中实际的密码哈希
        String dbPassword = "$2a$10$N.zmdr9k7uOCQb3Xwqy4/..zWC8BaZAeEnQbYPR7bvk2vJHCYyNbG";
        
        System.out.println("数据库密码: " + dbPassword);
        System.out.println("密码长度: " + dbPassword.length());
        System.out.println("是否以$2a$开头: " + dbPassword.startsWith("$2a$"));
        System.out.println("是否以$2a$10$开头: " + dbPassword.startsWith("$2a$10$"));
        
        // BCrypt密码应该是60个字符
        if (dbPassword.length() != 60) {
            System.out.println("警告：BCrypt密码长度应该是60个字符，当前长度: " + dbPassword.length());
        }
    }

    /**
     * 测试不同原始密码与数据库密码的匹配
     */
    @Test
    public void testMultiplePasswordMatches() {
        // 数据库中的加密密码（替换为实际值）
        String dbPassword = "$2a$10$N.zmdr9k7uOCQb3Xwqy4/..zWC8BaZAeEnQbYPR7bvk2vJHCYyNbG";
        
        // 尝试多个可能的原始密码
        String[] possiblePasswords = {
            "123456",
            "admin123",
            "password",
            "admin",
            "000000",
            "111111",
            "888888"
        };
        
        System.out.println("=== 测试多个密码匹配 ===");
        System.out.println("数据库密码: " + dbPassword);
        System.out.println();
        
        for (String password : possiblePasswords) {
            boolean matches = passwordEncoder.matches(password, dbPassword);
            System.out.println("测试密码: " + password + " -> " + (matches ? "✓ 匹配" : "✗ 不匹配"));
        }
    }
}



