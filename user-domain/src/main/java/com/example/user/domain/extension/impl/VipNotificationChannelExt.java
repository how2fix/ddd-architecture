package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.NotificationChannelExtPt;

import java.util.Arrays;
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
        switch (notificationType) {
            case MARKETING:
            case PROMOTION:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.EMAIL,
                        NotificationChannelExtPt.ChannelType.IN_APP);
            case TRANSACTION:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.SMS,
                        NotificationChannelExtPt.ChannelType.EMAIL);
            case SECURITY:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.SMS,
                        NotificationChannelExtPt.ChannelType.EMAIL);
            case SYSTEM:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.IN_APP,
                        NotificationChannelExtPt.ChannelType.PUSH);
            default:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.IN_APP);
        }
    }
}
