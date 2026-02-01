package com.example.user.domain.extension;

import java.math.BigDecimal;

/**
 * 用户等级升级扩展点接口
 * COLA架构：不同用户类型有不同升级规则
 */
public interface LevelUpgradeExtPt extends ExtensionPointI {

    /**
     * 检查是否可以升级
     *
     * @param userId          用户ID
     * @param currentLevel    当前等级
     * @param totalConsumption 累计消费金额
     * @param totalPoints     累计积分
     * @param bizScenario     业务场景
     * @return 升级结果
     */
    LevelUpgradeResult checkUpgrade(
            Long userId,
            String currentLevel,
            BigDecimal totalConsumption,
            Long totalPoints,
            BizScenario bizScenario
    );
}
