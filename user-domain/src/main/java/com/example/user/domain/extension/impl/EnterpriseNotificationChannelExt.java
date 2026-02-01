package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.NotificationChannelExtPt;

import java.util.Arrays;
import java.util.List;

import static com.example.user.domain.extension.NotificationChannelExtPt.ChannelType.*;

/**
 * 企业用户通知渠道
 * 企业用户享受最高优先级通知服务
 *
 * JDK 21 Feature: switch 表达式 + yield
 */
@Extension(bizId = "ENTERPRISE", description = "企业用户通知渠道")
public class EnterpriseNotificationChannelExt implements NotificationChannelExtPt {

    @Override
    public List<ChannelType> getChannels(NotificationType notificationType,
                                        Long userId, BizScenario bizScenario) {
        return switch (notificationType) {
            case MARKETING, PROMOTION -> Arrays.asList(EMAIL);
            case TRANSACTION -> Arrays.asList(SMS, EMAIL);
            case SECURITY -> {
                // 企业用户安全告警：电话 + 短信（最高优先级）
                yield Arrays.asList(PHONE, SMS);
            }
            case SYSTEM -> Arrays.asList(EMAIL);
        };
    }
}
