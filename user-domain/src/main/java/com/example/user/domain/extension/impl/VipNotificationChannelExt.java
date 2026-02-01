package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.NotificationChannelExtPt;

import java.util.List;

/**
 * VIP用户通知渠道
 * VIP用户享受更多通知渠道
 */
@Extension(bizId = "VIP", description = "VIP用户通知渠道")
public class VipNotificationChannelExt implements NotificationChannelExtPt {

    @Override
    public List<NotificationChannelExtPt.ChannelType> getChannels(NotificationChannelExtPt.NotificationType notificationType,
                                                                  Long userId, BizScenario bizScenario) {
        return switch (notificationType) {
            // 营销消息：邮件
            case MARKETING, PROMOTION -> List.of(NotificationChannelExtPt.ChannelType.EMAIL,
                    NotificationChannelExtPt.ChannelType.IN_APP);

            // 交易提醒：短信+邮件
            case TRANSACTION -> List.of(NotificationChannelExtPt.ChannelType.SMS,
                    NotificationChannelExtPt.ChannelType.EMAIL);

            // 安全告警：短信+邮件
            case SECURITY -> List.of(NotificationChannelExtPt.ChannelType.SMS,
                    NotificationChannelExtPt.ChannelType.EMAIL);

            // 系统公告：站内信+推送
            case SYSTEM -> List.of(NotificationChannelExtPt.ChannelType.IN_APP,
                    NotificationChannelExtPt.ChannelType.PUSH);
        };
    }
}
