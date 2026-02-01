package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.NotificationChannelExtPt;

import java.util.List;

/**
 * 企业用户通知渠道
 * 企业用户享受最高优先级通知服务
 */
@Extension(bizId = "ENTERPRISE", description = "企业用户通知渠道")
public class EnterpriseNotificationChannelExt implements NotificationChannelExtPt {

    @Override
    public List<NotificationChannelExtPt.ChannelType> getChannels(NotificationChannelExtPt.NotificationType notificationType,
                                                                  Long userId, BizScenario bizScenario) {
        return switch (notificationType) {
            // 营销消息：邮件
            case MARKETING, PROMOTION -> List.of(NotificationChannelExtPt.ChannelType.EMAIL);

            // 交易提醒：短信+邮件
            case TRANSACTION -> List.of(NotificationChannelExtPt.ChannelType.SMS,
                    NotificationChannelExtPt.ChannelType.EMAIL);

            // 安全告警：电话+短信（最高优先级）
            case SECURITY -> List.of(NotificationChannelExtPt.ChannelType.PHONE,
                    NotificationChannelExtPt.ChannelType.SMS);

            // 系统公告：邮件
            case SYSTEM -> List.of(NotificationChannelExtPt.ChannelType.EMAIL);
        };
    }
}
