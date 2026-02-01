package com.example.user.domain.extension;

import java.math.BigDecimal;

/**
 * 积分计算扩展点接口
 * COLA架构：不同业务场景有不同积分计算规则
 */
public interface PointCalculateExtPt extends ExtensionPointI {

    /**
     * 计算积分
     *
     * @param userId      用户ID
     * @param amount      金额/数量
     * @param bizScenario 业务场景
     * @return 获得的积分
     */
    Long calculate(Long userId, BigDecimal amount, BizScenario bizScenario);

    /**
     * 获取积分规则描述
     *
     * @param bizScenario 业务场景
     * @return 规则描述
     */
    default String getRuleDescription(BizScenario bizScenario) {
        return "积分规则";
    }
}
