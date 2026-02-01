package com.example.user.client.exception;

/**
 * 业务异常
 * COLA架构：可预期的业务异常，需要友好提示用户
 *
 * 使用场景：
 * - 用户不存在
 * - 邮箱已被注册
 * - 余额不足
 * - 权限不足
 *
 * 特点：
 * - 不需要告警
 * - 需要友好提示用户
 * - 可预期，可恢复
 */
public class BizException extends BaseException {

    private static final long serialVersionUID = 1L;

    public BizException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
    }

    public BizException(String errCode, String errMsg, Throwable cause) {
        super(errCode, errMsg, cause);
    }

    /**
     * 快速创建业务异常
     */
    public static BizException of(String errCode, String errMsg) {
        return new BizException(errCode, errMsg);
    }

    public static BizException of(ErrorCode errorCode) {
        return new BizException(errorCode);
    }
}
