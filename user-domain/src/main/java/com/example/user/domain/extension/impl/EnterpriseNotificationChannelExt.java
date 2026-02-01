package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.NotificationChannelExtPt;

import java.util.Arrays;
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
        switch (notificationType) {
            case MARKETING:
            case PROMOTION:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.EMAIL);
            case TRANSACTION:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.SMS,
                        NotificationChannelExtPt.ChannelType.EMAIL);
            case SECURITY:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.PHONE,
                        NotificationChannelExtPt.ChannelType.SMS);
            case SYSTEM:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.EMAIL);
            default:
                return Arrays.asList(NotificationChannelExtPt.ChannelType.EMAIL);
        }
    }
}
