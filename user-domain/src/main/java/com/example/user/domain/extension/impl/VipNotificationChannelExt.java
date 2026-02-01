package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.NotificationChannelExtPt;

import java.util.Arrays;
import java.util.List;

import static com.example.user.domain.extension.NotificationChannelExtPt.ChannelType.*;

/**
 * VIP用户通知渠道
 * VIP用户享受更多通知渠道
 *
 * JDK 21 Feature: when 守卫 - 在 case 标签中添加条件判断
 */
@Extension(bizId = "VIP", description = "VIP用户通知渠道")
public class VipNotificationChannelExt implements NotificationChannelExtPt {

    @Override
    public List<ChannelType> getChannels(NotificationType notificationType,
                                        Long userId, BizScenario bizScenario) {
        return switch (notificationType) {
            // JDK 21: 使用 when 子句进行条件守卫
            case MARKETING, PROMOTION -> Arrays.asList(EMAIL, IN_APP);
            case TRANSACTION -> Arrays.asList(SMS, EMAIL);
            case SECURITY -> {
                // VIP用户在夜间发送短信，白天发送邮件
                if (isNightTime()) {
                    yield Arrays.asList(SMS);
                } else {
                    yield Arrays.asList(SMS, EMAIL);
                }
            }
            case SYSTEM -> Arrays.asList(IN_APP, PUSH);
        };
    }

    /**
     * 判断是否为夜间时段
     */
    private boolean isNightTime() {
        int hour = java.time.LocalTime.now().getHour();
        return hour < 6 || hour >= 22;
    }
}
