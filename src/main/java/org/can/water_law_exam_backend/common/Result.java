package org.can.water_law_exam_backend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.can.water_law_exam_backend.common.constant.ResultCodeEnum;

/**
 * 统一返回结果封装类
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 状态码：0表示成功，1表示失败
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 成功返回（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回（带消息和数据）
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败返回
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ResultCodeEnum.FAIL.getCode(), msg, null);
    }

    /**
     * 失败返回（自定义状态码）
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 失败返回（带数据）
     */
    public static <T> Result<T> error(Integer code, String msg, T data) {
        return new Result<>(code, msg, data);
    }
}



