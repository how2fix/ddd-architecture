package com.example.user.domain.extension;

import java.util.List;

/**
 * 通知渠道扩展点接口
 * COLA架构：根据用户类型和消息类型选择通知渠道
 */
public interface NotificationChannelExtPt extends ExtensionPointI {

    /**
     * 获取通知渠道列表
     *
     * @param notificationType 通知类型
     * @param userId            用户ID
     * @param bizScenario       业务场景
     * @return 通知渠道列表
     */
    List<ChannelType> getChannels(NotificationType notificationType, Long userId, BizScenario bizScenario);

    /**
     * 通知渠道类型
     */
    enum ChannelType {
        IN_APP,      // 站内信
        SMS,         // 短信
        EMAIL,       // 邮件
        PUSH,        // 推送
        PHONE,       // 电话
        WECHAT       // 微信
    }

    /**
     * 通知类型
     */
    enum NotificationType {
        MARKETING,     // 营销消息
        TRANSACTION,   // 交易提醒
        SECURITY,      // 安全告警
        SYSTEM,        // 系统公告
        PROMOTION      // 促销活动
    }
}
