package com.example.user.client.constant;

/**
 * 错误码常量
 */
public enum ErrorCode {

    // 用户相关错误码 (1xxxx)
    USER_NOT_FOUND("10001", "用户不存在"),
    USER_ALREADY_EXISTS("10002", "用户已存在"),
    EMAIL_ALREADY_EXISTS("10003", "邮箱已被注册"),
    PHONE_ALREADY_EXISTS("10004", "手机号已被注册"),
    INVALID_PASSWORD("10005", "密码格式不正确"),
    USER_FROZEN("10006", "用户已被冻结"),

    // 通知相关错误码 (2xxxx)
    EMAIL_SEND_FAILED("20001", "邮件发送失败"),
    SMS_SEND_FAILED("20002", "短信发送失败"),

    // 系统错误码 (5xxxx)
    SYSTEM_ERROR("50000", "系统错误"),
    PARAM_INVALID("50001", "参数校验失败");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
