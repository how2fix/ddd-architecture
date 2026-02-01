package com.example.user.client.exception;

/**
 * 异常基类
 * COLA架构：统一异常处理的基础
 *
 * JDK 21 Feature: Sealed 类 - 限制继承层次，增强类型安全
 * - permits 只允许 BizException 和 SysException 继承
 * - 编译时强制检查，确保类型安全
 */
public sealed abstract class BaseException extends RuntimeException
        permits BizException, SysException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final String errCode;

    /**
     * 错误信息
     */
    private final String errMsg;

    protected BaseException(String errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    protected BaseException(String errCode, String errMsg, Throwable cause) {
        super(errMsg, cause);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    /**
     * JDK 21 Feature: Pattern matching for switch
     * 可以使用 switch 模式匹配来处理异常
     */
    public String recoverableMessage() {
        return switch (this) {
            case BizException e -> "业务异常，可恢复: " + e.getErrMsg();
            case SysException e -> "系统异常，需要技术介入: " + e.getErrCode();
        };
    }
}
