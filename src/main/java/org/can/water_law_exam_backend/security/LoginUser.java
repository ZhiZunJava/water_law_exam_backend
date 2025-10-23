package org.can.water_law_exam_backend.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security用户详情类
 */
@Data
public class LoginUser implements UserDetails {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String user_no;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 用户类型（admin/user）
     */
    private String userType;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 权限列表
     */
    private Collection<? extends GrantedAuthority> authorities;

    public LoginUser() {
    }

    public LoginUser(Long userId, String user_no, String password, String name, String userType, Boolean enabled) {
        this.userId = userId;
        this.user_no = user_no;
        this.password = password;
        this.name = name;
        this.userType = userType;
        this.enabled = enabled;
        this.authorities = Collections.emptyList();
    }

    public LoginUser(Long userId, String user_no, String password, String name, String userType, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.user_no = user_no;
        this.password = password;
        this.name = name;
        this.userType = userType;
        this.enabled = true;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities != null ? this.authorities : Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.user_no;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}


