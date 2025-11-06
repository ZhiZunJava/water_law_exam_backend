package org.can.water_law_exam_backend.exception;

import lombok.Getter;
import org.can.water_law_exam_backend.common.constant.ResultCodeEnum;

/**
 * 业务异常类
 *
 * @author 程安宁
 * @date 2025/11/06
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCodeEnum.FAIL.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCodeEnum.FAIL.getCode();
    }
}



