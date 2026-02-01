package com.example.user.domain.extension;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 等级升级结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelUpgradeResult {

    /**
     * 是否可以升级
     */
    private boolean canUpgrade;

    /**
     * 目标等级
     */
    private String targetLevel;

    /**
     * 升级原因/说明
     */
    private String reason;

    /**
     * 升级所需额外消费金额（null表示不需要）
     */
    private Long requiredAmount;

    /**
     * 创建不可升级结果
     */
    public static LevelUpgradeResult notMet(String targetLevel, Long requiredAmount) {
        return LevelUpgradeResult.builder()
                .canUpgrade(false)
                .targetLevel(targetLevel)
                .reason(String.format("还差%d元可升级到%s", requiredAmount, targetLevel))
                .requiredAmount(requiredAmount)
                .build();
    }

    /**
     * 创建不可升级结果（无具体金额）
     */
    public static LevelUpgradeResult notMet(String reason) {
        return LevelUpgradeResult.builder()
                .canUpgrade(false)
                .reason(reason)
                .build();
    }

    /**
     * 创建可升级结果
     */
    public static LevelUpgradeResult success(String targetLevel, String reason) {
        return LevelUpgradeResult.builder()
                .canUpgrade(true)
                .targetLevel(targetLevel)
                .reason(reason)
                .build();
    }
}
