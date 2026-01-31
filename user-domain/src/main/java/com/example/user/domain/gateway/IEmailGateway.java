package com.example.user.domain.gateway;

/**
 * 邮件网关接口
 * 防腐层(ACL)：隔离第三方邮件服务依赖
 * Domain层定义接口，Infrastructure层实现
 */
public interface IEmailGateway {

    /**
     * 发送欢迎邮件
     *
     * @param to      收件人邮箱
     * @param username 用户名
     * @return 是否发送成功
     */
    boolean sendWelcomeEmail(String to, String username);

    /**
     * 发送激活邮件
     *
     * @param to      收件人邮箱
     * @param code    激活码
     * @return 是否发送成功
     */
    boolean sendActivationEmail(String to, String code);

    /**
     * 发送验证码邮件
     *
     * @param to      收件人邮箱
     * @param code    验证码
     * @return 是否发送成功
     */
    boolean sendVerificationEmail(String to, String code);
}
