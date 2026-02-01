package com.example.user.client.exception;

/**
 * 错误码枚举
 * COLA架构：统一的错误码定义
 *
 * 错误码规范：
 * - 1xxxx: 用户相关业务错误
 * - 2xxxx: 通知相关业务错误
 * - 3xxxx: 订单相关业务错误
 * - 5xxxx: 系统错误
 */
public enum ErrorCode {

    // ==================== 用户相关错误码 (1xxxx) ====================
    USER_NOT_FOUND("10001", "用户不存在", ErrorLevel.BIZ),
    USER_ALREADY_EXISTS("10002", "用户已存在", ErrorLevel.BIZ),
    EMAIL_ALREADY_EXISTS("10003", "邮箱已被注册", ErrorLevel.BIZ),
    PHONE_ALREADY_EXISTS("10004", "手机号已被注册", ErrorLevel.BIZ),
    INVALID_PASSWORD("10005", "密码格式不正确", ErrorLevel.BIZ),
    USER_FROZEN("10006", "用户已被冻结", ErrorLevel.BIZ),
    USER_INACTIVE("10007", "用户未激活", ErrorLevel.BIZ),

    // ==================== 通知相关错误码 (2xxxx) ====================
    EMAIL_SEND_FAILED("20001", "邮件发送失败", ErrorLevel.BIZ),
    SMS_SEND_FAILED("20002", "短信发送失败", ErrorLevel.BIZ),

    // ==================== 系统错误码 (5xxxx) ====================
    SYSTEM_ERROR("50000", "系统错误", ErrorLevel.SYS),
    PARAM_INVALID("50001", "参数校验失败", ErrorLevel.BIZ),
    DB_ERROR("50002", "数据库异常", ErrorLevel.SYS),
    EXTERNAL_SERVICE_ERROR("50003", "外部服务调用失败", ErrorLevel.SYS),
    CONFIG_ERROR("50004", "配置错误", ErrorLevel.SYS);

    private final String code;
    private final String message;
    private final ErrorLevel level;

    ErrorCode(String code, String message, ErrorLevel level) {
        this.code = code;
        this.message = message;
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ErrorLevel getLevel() {
        return level;
    }

    /**
     * 转换为业务异常
     */
    public BizException toException() {
        return new BizException(this);
    }

    /**
     * 转换为业务异常（带自定义消息）
     */
    public BizException toException(String customMessage) {
        return new BizException(this.code, customMessage);
    }

    /**
     * 错误级别
     */
    public enum ErrorLevel {
        BIZ,  // 业务异常
        SYS   // 系统异常
    }
}
