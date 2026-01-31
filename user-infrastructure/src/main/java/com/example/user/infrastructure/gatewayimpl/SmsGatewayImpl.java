package com.example.user.infrastructure.gatewayimpl;

import com.example.user.domain.gateway.ISmsGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 短信网关实现
 * 实现Domain层定义的ISmsGateway接口
 * 这是一个模拟实现，实际项目中应调用真实短信服务（如阿里云、腾讯云）
 */
@Slf4j
@Component
public class SmsGatewayImpl implements ISmsGateway {

    @Override
    public boolean sendRegisterSms(String phone, String username) {
        log.info("========== 短信模拟发送 ==========");
        log.info("手机号: {}", phone);
        log.info("类型: 注册成功通知");
        log.info("内容:");
        log.info("  尊敬的{}，恭喜您成功注册！", username);
        log.info("  感谢您的使用。");
        log.info("================================");
        return true;
    }

    @Override
    public boolean sendVerificationSms(String phone, String code) {
        log.info("========== 短信模拟发送 ==========");
        log.info("手机号: {}", phone);
        log.info("类型: 验证码短信");
        log.info("内容:");
        log.info("  您的验证码是: {}", code);
        log.info("  验证码5分钟内有效，请勿泄露。");
        log.info("================================");
        return true;
    }

    @Override
    public boolean sendNotificationSms(String phone, String message) {
        log.info("========== 短信模拟发送 ==========");
        log.info("手机号: {}", phone);
        log.info("类型: 通知短信");
        log.info("内容: {}", message);
        log.info("================================");
        return true;
    }
}
