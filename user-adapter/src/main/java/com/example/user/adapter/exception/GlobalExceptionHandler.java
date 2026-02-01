package com.example.user.adapter.exception;

import com.example.user.client.dto.Response;
import com.example.user.client.exception.BizException;
import com.example.user.client.exception.BaseException;
import com.example.user.client.exception.SysException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * COLA架构：统一异常处理，将异常转换为Response
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     * 特点：可预期，不需要告警，友好提示用户
     */
    @ExceptionHandler(BizException.class)
    public Response handleBizException(BizException e) {
        log.warn("业务异常: code={}, message={}", e.getErrCode(), e.getErrMsg());
        return Response.buildFailure(e.getErrCode(), e.getErrMsg());
    }

    /**
     * 系统异常处理
     * 特点：不可预期，需要告警，不暴露详细错误给用户
     */
    @ExceptionHandler(SysException.class)
    public Response handleSysException(SysException e) {
        log.error("系统异常: code={}, message={}", e.getErrCode(), e.getErrMsg(), e);
        // 系统异常不向用户暴露详细信息
        return Response.buildFailure(e.getErrCode(), "系统繁忙，请稍后重试");
    }

    /**
     * 其他异常处理
     * 默认按系统异常处理
     */
    @ExceptionHandler(Exception.class)
    public Response handleException(Exception e) {
        log.error("未知异常: message={}", e.getMessage(), e);
        return Response.buildFailure("SYSTEM_ERROR", "系统繁忙，请稍后重试");
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Response handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数校验失败: message={}", e.getMessage());
        return Response.buildFailure("PARAM_INVALID", e.getMessage());
    }

    /**
     * 非法状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    public Response handleIllegalStateException(IllegalStateException e) {
        log.warn("非法状态: message={}", e.getMessage());
        return Response.buildFailure("ILLEGAL_STATE", e.getMessage());
    }
}
