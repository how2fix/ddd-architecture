package com.example.user.domain.gateway;

/**
 * 短信网关接口
 * 防腐层(ACL)：隔离第三方短信服务依赖
 * Domain层定义接口，Infrastructure层实现
 */
public interface ISmsGateway {

    /**
     * 发送注册成功短信
     *
     * @param phone    手机号
     * @param username 用户名
     * @return 是否发送成功
     */
    boolean sendRegisterSms(String phone, String username);

    /**
     * 发送验证码短信
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否发送成功
     */
    boolean sendVerificationSms(String phone, String code);

    /**
     * 发送通知短信
     *
     * @param phone   手机号
     * @param message 短信内容
     * @return 是否发送成功
     */
    boolean sendNotificationSms(String phone, String message);
}
