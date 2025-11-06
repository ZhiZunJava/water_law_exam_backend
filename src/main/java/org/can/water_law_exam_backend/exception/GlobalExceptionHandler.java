package org.can.water_law_exam_backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.can.water_law_exam_backend.common.constant.ResultCodeEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     *
     * @param e e
     * @return {@link Result }<{@link Void }>
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     *
     * @param e e
     * @return {@link Result }<{@link Void }>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数校验异常：{}", message);
        return Result.error(ResultCodeEnum.PARAM_ERROR.getCode(), message);
    }

    /**
     * 绑定异常
     *
     * @param e e
     * @return {@link Result }<{@link Void }>
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数绑定异常：{}", message);
        return Result.error(ResultCodeEnum.PARAM_ERROR.getCode(), message);
    }

    /**
     * 用户名未找到异常
     *
     * @param e e
     * @return {@link Result }<{@link Void }>
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public Result<Void> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("用户名未找到：{}", e.getMessage());
        return Result.error(ResultCodeEnum.UNAUTHORIZED.getCode(), "用户名或密码错误");
    }

    /**
     * 认证失败异常
     *
     * @param e e
     * @return {@link Result }<{@link Void }>
     */
    @ExceptionHandler(BadCredentialsException.class)
    public Result<Void> handleBadCredentialsException(BadCredentialsException e) {
        log.error("认证失败：{}", e.getMessage());
        return Result.error(ResultCodeEnum.UNAUTHORIZED.getCode(), "用户名或密码错误");
    }

    /**
     * 账户被锁定异常
     *
     * @param e e
     * @return {@link Result }<{@link Void }>
     */
    @ExceptionHandler(LockedException.class)
    public Result<Void> handleLockedException(LockedException e) {
        log.error("账户被锁定：{}", e.getMessage());
        return Result.error(ResultCodeEnum.FORBIDDEN.getCode(), "账户已被禁用");
    }

    /**
     * 账户被禁用异常
     *
     * @param e e
     * @return {@link Result }<{@link Void }>
     */
    @ExceptionHandler(DisabledException.class)
    public Result<Void> handleDisabledException(DisabledException e) {
        log.error("账户被禁用：{}", e.getMessage());
        return Result.error(ResultCodeEnum.FORBIDDEN.getCode(), "账户已被禁用");
    }

    /**
     * 访问拒绝异常
     *
     * @param e e
     * @return {@link Result }<{@link Void }>
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("访问拒绝：{}", e.getMessage());
        return Result.error(ResultCodeEnum.FORBIDDEN.getCode(), "没有权限访问该资源");
    }

    /**
     * 运行时异常
     *
     * @param e e
     * @return {@link Result }<{@link Void }>
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：", e);
        return Result.error("系统异常，请联系管理员");
    }

    /**
     * 通用异常
     *
     * @param e e
     * @return {@link Result }<{@link Void }>
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error("系统异常，请联系管理员");
    }
}

