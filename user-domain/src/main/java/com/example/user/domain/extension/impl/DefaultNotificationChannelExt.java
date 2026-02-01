package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.NotificationChannelExtPt;

import java.util.Arrays;
import java.util.List;

import static com.example.user.domain.extension.NotificationChannelExtPt.ChannelType.IN_APP;
import static com.example.user.domain.extension.NotificationChannelExtPt.ChannelType.SMS;

/**
 * 普通用户通知渠道
 *
 * JDK 21 Feature: 简化 switch 表达式
 */
@Extension(bizId = "DEFAULT", description = "普通用户通知渠道")
public class DefaultNotificationChannelExt implements NotificationChannelExtPt {

    @Override
    public List<ChannelType> getChannels(NotificationType notificationType,
                                        Long userId, BizScenario bizScenario) {
        return switch (notificationType) {
            case MARKETING, PROMOTION -> Arrays.asList(IN_APP);
            case TRANSACTION -> Arrays.asList(IN_APP);
            case SECURITY -> Arrays.asList(SMS);
            case SYSTEM -> Arrays.asList(IN_APP);
        };
    }
}
