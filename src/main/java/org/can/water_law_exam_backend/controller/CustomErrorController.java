package org.can.water_law_exam_backend.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.can.water_law_exam_backend.common.Result;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统一错误处理控制器
 * 处理Spring Boot默认的/error路径，返回JSON格式错误信息
 */
@Slf4j
@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public Result<Void> handleError(HttpServletRequest request) {
        // 获取错误状态码
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            
            log.warn("错误处理：状态码={}, 请求路径={}, 错误信息={}", statusCode, requestUri, message);
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return Result.error(404, "请求的资源不存在：" + (requestUri != null ? requestUri : "未知路径"));
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return Result.error(403, "没有权限访问该资源");
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                return Result.error(401, "未登录或登录已过期，请重新登录");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return Result.error(500, "服务器内部错误");
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                return Result.error(400, "请求参数错误");
            } else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                return Result.error(405, "不支持的请求方法");
            }
            
            return Result.error(statusCode, "请求失败：" + (message != null ? message : "未知错误"));
        }
        
        return Result.error(500, "未知错误");
    }
}



