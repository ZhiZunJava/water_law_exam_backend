package org.can.water_law_exam_backend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.entity.Admin;
import org.can.water_law_exam_backend.entity.AccountUser;
import org.can.water_law_exam_backend.mapper.AdminMapper;
import org.can.water_law_exam_backend.mapper.AccountUserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义用户详情服务
 * 实现账号密码登录认证
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminMapper adminMapper;
    private final AccountUserMapper accountUserMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 根据用户名加载用户信息
     * 用户名格式：userType:username
     * 例如：admin:admin 或 user:110101199001011234
     *
     * @param username 用户名（带类型前缀）
     * @return UserDetails
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 解析用户类型和用户名
        String[] parts = username.split(":", 2);
        if (parts.length != 2) {
            throw new UsernameNotFoundException("用户名格式错误");
        }

        String userType = parts[0];
        String actualUsername = parts[1];

        // 根据用户类型查询不同的表
        if ("admin".equals(userType)) {
            return loadAdminUser(actualUsername);
        } else if ("user".equals(userType)) {
            return loadAccountUser(actualUsername);
        } else {
            throw new UsernameNotFoundException("不支持的用户类型");
        }
    }

    /**
     * 加载管理员用户
     *
     * @param userNo 管理员账号
     * @return LoginUser
     */
    private LoginUser loadAdminUser(String userNo) {
        Admin admin = adminMapper.selectByUserNo(userNo);
        if (admin == null) {
            throw new UsernameNotFoundException("管理员不存在");
        }

        if (admin.getLocked()) {
            throw new UsernameNotFoundException("账号已被禁用");
        }

        // 检测并处理明文密码
        String password = admin.getPwd();
        if (!isPasswordEncrypted(password)) {
            log.info("管理员 {} 密码自动加密", userNo);
            String encryptedPassword = passwordEncoder.encode(password);
            adminMapper.updatePassword(admin.getId(), encryptedPassword);
            password = encryptedPassword;
        }

        // 创建权限列表（管理员拥有ROLE_ADMIN角色）
        return new LoginUser(
                admin.getId(),
                admin.getUserNo(),
                password,
                admin.getName(),
                "admin",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    /**
     * 加载学员用户
     *
     * @param idNo 身份证号
     * @return LoginUser
     */
    private LoginUser loadAccountUser(String idNo) {
        AccountUser user = accountUserMapper.selectByIdNo(idNo);
        if (user == null) {
            throw new UsernameNotFoundException("学员不存在");
        }

        if (user.getLocked()) {
            throw new UsernameNotFoundException("账号已被禁用");
        }

        // 检测并处理明文密码
        String password = user.getPwd();
        if (!isPasswordEncrypted(password)) {
            log.info("学员 {} 密码自动加密", idNo);
            String encryptedPassword = passwordEncoder.encode(password);
            accountUserMapper.updatePassword(user.getId(), encryptedPassword);
            password = encryptedPassword;
        }

        // 创建权限列表（学员拥有ROLE_USER角色）
        return new LoginUser(
                user.getId(),
                user.getIdNo(),
                password,
                user.getName(),
                "user",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    /**
     * 判断密码是否已加密
     * BCrypt 加密后的密码格式：$2a$10$... 或 $2b$10$... 或 $2y$10$...
     * 长度通常为 60 字符
     *
     * @param password 密码
     * @return true-已加密，false-明文
     */
    private boolean isPasswordEncrypted(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        // BCrypt 加密后的密码特征：
        // 1. 以 $2a$、$2b$ 或 $2y$ 开头
        // 2. 长度通常为 60 字符
        return password.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }
}

