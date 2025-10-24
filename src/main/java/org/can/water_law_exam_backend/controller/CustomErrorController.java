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
 * 自定义错误处理器
 * 替代废弃的 throw-exception-if-no-handler-found 配置
 * 用于统一处理 404、500 等 HTTP 错误
 */
@Slf4j
@RestController
public class CustomErrorController implements ErrorController {

    /**
     * 统一错误处理端点
     */
    @RequestMapping("/error")
    public Result<Void> handleError(HttpServletRequest request) {
        // 获取错误状态码
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            // 获取请求的原始 URI
            String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            if (requestUri == null) {
                requestUri = request.getRequestURI();
            }
            
            // 根据不同的状态码返回不同的错误信息
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                log.warn("404错误：请求路径 {} 不存在", requestUri);
                return Result.error(404, "请求的资源不存在：" + requestUri);
            } 
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                // 获取异常信息
                Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
                if (throwable != null) {
                    log.error("500错误：服务器内部错误", throwable);
                } else {
                    log.error("500错误：服务器内部错误，路径：{}", requestUri);
                }
                return Result.error(500, "服务器内部错误");
            }
            else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                log.warn("403错误：没有权限访问 {}", requestUri);
                return Result.error(403, "没有权限访问该资源");
            }
            else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                log.warn("401错误：未授权访问 {}", requestUri);
                return Result.error(401, "未登录或登录已过期，请重新登录");
            }
            else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                log.warn("405错误：不支持的请求方法，路径：{}", requestUri);
                return Result.error(405, "不支持的请求方法");
            }
            else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                log.warn("400错误：错误的请求参数，路径：{}", requestUri);
                return Result.error(400, "错误的请求参数");
            }
            else {
                log.error("HTTP错误：状态码 {}，路径：{}", statusCode, requestUri);
                return Result.error(statusCode, "请求处理失败");
            }
        }
        
        // 未知错误
        log.error("未知错误：无法获取错误状态码");
        return Result.error("未知错误");
    }
}

