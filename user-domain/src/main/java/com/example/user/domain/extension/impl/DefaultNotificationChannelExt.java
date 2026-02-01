package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.NotificationChannelExtPt;

import java.util.Arrays;
import java.util.List;

/**
 * 普通用户通知渠道
 */
@Extension(bizId = "DEFAULT", description = "普通用户通知渠道")
public class DefaultNotificationChannelExt implements NotificationChannelExtPt {

    @Override
    public List<NotificationChannelExtPt.ChannelType> getChannels(NotificationChannelExtPt.NotificationType notificationType,
                                                                  Long userId, BizScenario bizScenario) {
        switch (notificationType) {
            case MARKETING:
            case PROMOTION:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.IN_APP);
            case TRANSACTION:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.IN_APP);
            case SECURITY:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.SMS);
            case SYSTEM:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.IN_APP);
            default:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.IN_APP);
        }
    }
}
