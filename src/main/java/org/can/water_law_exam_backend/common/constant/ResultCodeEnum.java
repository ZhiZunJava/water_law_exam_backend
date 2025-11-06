package org.can.water_law_exam_backend.common.constant;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Getter
public enum ResultCodeEnum {

    /**
     * 成功
     */
    SUCCESS(0, "操作成功"),

    /**
     * 失败
     */
    FAIL(1, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(40001, "用户名或密码错误"),

    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(40002, "验证码错误或已过期"),

    /**
     * Token无效
     */
    TOKEN_INVALID(40101, "Token无效"),

    /**
     * Token过期
     */
    TOKEN_EXPIRED(40102, "Token已过期"),

    /**
     * 数据已存在
     */
    DATA_EXIST(40003, "数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(40004, "数据不存在");

    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

