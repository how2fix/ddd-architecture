package com.example.user.client.exception;

/**
 * 系统异常
 * COLA架构：不可预期的系统异常，需要告警
 *
 * 使用场景：
 * - 数据库连接失败
 * - 外部服务调用失败
 * - 网络超时
 * - 配置错误
 *
 * 特点：
 * - 需要告警
 * - 不需要向用户展示详细信息
 * - 不可预期，需要技术介入
 */
public class SysException extends BaseException {

    private static final long serialVersionUID = 1L;

    public SysException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public SysException(String errCode, String errMsg, Throwable cause) {
        super(errCode, errMsg, cause);
    }

    /**
     * 快速创建系统异常（带原始异常）
     */
    public static SysException of(String errCode, String errMsg, Throwable cause) {
        return new SysException(errCode, errMsg, cause);
    }

    /**
     * 快速创建系统异常（无原始异常）
     */
    public static SysException of(String errCode, String errMsg) {
        return new SysException(errCode, errMsg);
    }

    /**
     * 从原始异常包装为系统异常
     */
    public static SysException wrap(Throwable cause) {
        return new SysException("SYSTEM_ERROR", "系统异常: " + cause.getMessage(), cause);
    }

    public static SysException wrap(String errCode, Throwable cause) {
        return new SysException(errCode, "系统异常: " + cause.getMessage(), cause);
    }
}
