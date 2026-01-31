package com.example.user.infrastructure.gatewayimpl;

import com.example.user.domain.gateway.IEmailGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 邮件网关实现
 * 实现Domain层定义的IEmailGateway接口
 * 这是一个模拟实现，实际项目中应调用真实邮件服务
 */
@Slf4j
@Component
public class EmailGatewayImpl implements IEmailGateway {

    @Override
    public boolean sendWelcomeEmail(String to, String username) {
        log.info("========== 邮件模拟发送 ==========");
        log.info("收件人: {}", to);
        log.info("类型: 欢迎邮件");
        log.info("内容:");
        log.info("  尊敬的 {}，", username);
        log.info("  欢迎注册成为我们的会员！");
        log.info("  请点击链接激活您的账号。");
        log.info("================================");
        return true;
    }

    @Override
    public boolean sendActivationEmail(String to, String code) {
        log.info("========== 邮件模拟发送 ==========");
        log.info("收件人: {}", to);
        log.info("类型: 激活邮件");
        log.info("内容:");
        log.info("  您的激活码是: {}", code);
        log.info("  请在30分钟内完成激活。");
        log.info("================================");
        return true;
    }

    @Override
    public boolean sendVerificationEmail(String to, String code) {
        log.info("========== 邮件模拟发送 ==========");
        log.info("收件人: {}", to);
        log.info("类型: 验证码邮件");
        log.info("内容:");
        log.info("  您的验证码是: {}", code);
        log.info("  验证码5分钟内有效。");
        log.info("================================");
        return true;
    }
}
