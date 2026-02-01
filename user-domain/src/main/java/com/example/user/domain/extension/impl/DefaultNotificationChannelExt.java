package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.NotificationChannelExtPt;

import java.util.List;

/**
 * 普通用户通知渠道
 */
@Extension(bizId = "DEFAULT", description = "普通用户通知渠道")
public class DefaultNotificationChannelExt implements NotificationChannelExtPt {

    @Override
    public List<NotificationChannelExtPt.ChannelType> getChannels(NotificationChannelExtPt.NotificationType notificationType,
                                                                  Long userId, BizScenario bizScenario) {
        return switch (notificationType) {
            // 营销消息：站内信
            case MARKETING, PROMOTION -> List.of(NotificationChannelExtPt.ChannelType.IN_APP);

            // 交易提醒：站内信
            case TRANSACTION -> List.of(NotificationChannelExtPt.ChannelType.IN_APP);

            // 安全告警：短信
            case SECURITY -> List.of(NotificationChannelExtPt.ChannelType.SMS);

            // 系统公告：站内信
            case SYSTEM -> List.of(NotificationChannelExtPt.ChannelType.IN_APP);
        };
    }
}
