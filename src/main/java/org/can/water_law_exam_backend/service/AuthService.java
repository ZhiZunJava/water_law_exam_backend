package org.can.water_law_exam_backend.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.config.JwtProperties;
import org.can.water_law_exam_backend.dto.request.auth.LoginRequest;
import org.can.water_law_exam_backend.dto.response.auth.CurrentUserResponse;
import org.can.water_law_exam_backend.dto.response.auth.LoginResponse;
import org.can.water_law_exam_backend.entity.AccountUser;
import org.can.water_law_exam_backend.entity.Admin;
import org.can.water_law_exam_backend.exception.BusinessException;
import org.can.water_law_exam_backend.mapper.AccountUserMapper;
import org.can.water_law_exam_backend.mapper.AdminMapper;
import org.can.water_law_exam_backend.security.LoginUser;
import org.can.water_law_exam_backend.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 认证服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final CaptchaService captchaService;
    private final TokenService tokenService;
    private final AdminMapper adminMapper;
    private final AccountUserMapper accountUserMapper;

    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @return 登录响应（包含Token）
     */
    public LoginResponse adminLogin(LoginRequest request) {
        return login(request, "admin");
    }

    /**
     * 学员登录（身份证号+密码）
     *
     * @param request 登录请求
     * @return 登录响应（包含Token）
     */
    public LoginResponse userLogin(LoginRequest request) {
        return login(request, "user");
    }

    /**
     * 通用登录逻辑
     *
     * @param request  登录请求
     * @param userType 用户类型（admin或user）
     * @return 登录响应
     */
    private LoginResponse login(LoginRequest request, String userType) {
        // 1. 验证验证码
        boolean captchaValid = captchaService.verifyCaptcha(
                request.getCaptchaId(),
                request.getCaptchaCode()
        );
        if (!captchaValid) {
            log.warn("验证码验证失败：用户={}, 验证码ID={}", request.getUsername(), request.getCaptchaId());
            throw new BusinessException(400, "验证码错误或已过期");
        }

        try {
            // 2. 构造用户名（格式：userType:username）
            // 例如：admin:admin 或 user:身份证号
            String fullUsername = userType + ":" + request.getUsername();

            // 创建认证Token
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(fullUsername, request.getPassword());

            // 执行认证
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 认证成功，获取用户信息
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            // 生成JWT Token
            String token = jwtUtil.generateToken(
                    loginUser.getUserId(),
                    loginUser.getUsername(),
                    loginUser.getUserType()
            );

            // 将token存储到Redis（实现token管理）
            tokenService.saveToken(token, loginUser.getUserId());

            log.info("用户登录成功：{}, 类型：{}", loginUser.getUsername(), userType);

            // 返回登录响应
            return new LoginResponse(
                    token,
                    loginUser.getUserId(),
                    loginUser.getUsername(),
                    loginUser.getName(),
                    loginUser.getUserType(),
                    jwtProperties.getTokenPrefix(),
                    jwtProperties.getExpiration()
            );

        } catch (AuthenticationException e) {
            log.warn("认证失败：用户={}, 类型={}, 原因={}", request.getUsername(), userType, e.getMessage());
            throw new BusinessException(401, "用户名或密码错误");
        } catch (BusinessException e) {
            // 重新抛出业务异常，保持原有的错误码和消息
            throw e;
        } catch (Exception e) {
            log.error("登录异常：用户={}, 类型={}", request.getUsername(), userType, e);
            throw new BusinessException(500, "登录失败，请稍后重试");
        }
    }

    /**
     * 获取当前登录用户详细信息
     *
     * @param token JWT Token
     * @return 当前用户详细信息
     */
    public CurrentUserResponse getCurrentUser(String token) {
        try {
            // 1. 验证Token有效性
            if (!jwtUtil.validateToken(token)) {
                throw new BusinessException(401, "Token无效或已过期");
            }

            // 2. 验证Token是否在Redis中存在
            if (!tokenService.validateToken(token)) {
                throw new BusinessException(401, "Token已失效，请重新登录");
            }

            // 3. 从Token中解析用户信息
            Claims claims = jwtUtil.getClaimsFromToken(token);
            if (claims == null) {
                throw new BusinessException(401, "Token解析失败");
            }
            
            // 获取用户ID（兼容Integer和Long类型）
            Long userId;
            Object userIdObj = claims.get("userId");
            if (userIdObj instanceof Integer) {
                userId = ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            } else {
                throw new BusinessException(401, "Token格式错误");
            }
            
            String username = claims.getSubject();
            String userType = claims.get("userType", String.class);

            // 4. 根据用户类型查询详细信息
            if ("admin".equals(userType)) {
                return buildAdminResponse(userId, token, claims);
            } else if ("user".equals(userType)) {
                return buildUserResponse(userId, token, claims);
            } else {
                throw new BusinessException(400, "未知的用户类型");
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            throw new BusinessException(500, "获取用户信息失败");
        }
    }

    /**
     * 构建管理员详细信息响应
     */
    private CurrentUserResponse buildAdminResponse(Long userId, String token, Claims claims) {
        Admin admin = adminMapper.selectById(userId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        if (Boolean.TRUE.equals(admin.getLocked())) {
            throw new BusinessException(403, "该账号已被禁用");
        }

        // 计算Token过期时间
        Date expiration = claims.getExpiration();
        Long tokenExpireAt = expiration != null ? expiration.getTime() : null;

        return CurrentUserResponse.builder()
                .id(admin.getId())
                .username(claims.getSubject())
                .name(admin.getName())
                .userType("ADMIN")
                .userNo(admin.getUserNo())
                .locked(admin.getLocked())
                .createTime(admin.getCreateTime())
                .updateTime(admin.getUpdateTime())
                .token(token)
                .tokenExpireAt(tokenExpireAt)
                .build();
    }

    /**
     * 构建学员详细信息响应
     */
    private CurrentUserResponse buildUserResponse(Long userId, String token, Claims claims) {
        AccountUser user = accountUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "学员不存在");
        }

        if (Boolean.TRUE.equals(user.getLocked())) {
            throw new BusinessException(403, "该账号已被禁用");
        }

        // 计算Token过期时间
        Date expiration = claims.getExpiration();
        Long tokenExpireAt = expiration != null ? expiration.getTime() : null;

        return CurrentUserResponse.builder()
                .id(user.getId())
                .username(claims.getSubject())
                .name(user.getName())
                .userType("USER")
                .idNo(user.getIdNo())
                .phone(user.getPhone())
                .orgId(user.getOrgId())
                .orgName(user.getOrgName())
                .locked(user.getLocked())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .token(token)
                .tokenExpireAt(tokenExpireAt)
                .build();
    }
}

