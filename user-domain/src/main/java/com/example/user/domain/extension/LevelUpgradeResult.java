package com.example.user.domain.extension;

/**
 * 等级升级结果
 *
 * JDK 21 Feature: Record 类 - 不可变数据载体
 * - 替代 Lombok @Data + @Builder + @AllArgsConstructor + @NoArgsConstructor
 * - 自动生成构造器、getter、equals、hashCode、toString
 * - 更简洁、更安全
 */
public record LevelUpgradeResult(
    boolean canUpgrade,
    String targetLevel,
    String reason,
    Long requiredAmount
) {

    /**
     * 创建不可升级结果
     */
    public static LevelUpgradeResult notMet(String targetLevel, Long requiredAmount) {
        return new LevelUpgradeResult(
            false,
            targetLevel,
            "还差%d元可升级到%s".formatted(requiredAmount, targetLevel),
            requiredAmount
        );
    }

    /**
     * 创建不可升级结果（无具体金额）
     */
    public static LevelUpgradeResult notMet(String reason) {
        return new LevelUpgradeResult(false, null, reason, null);
    }

    /**
     * 创建可升级结果
     */
    public static LevelUpgradeResult success(String targetLevel, String reason) {
        return new LevelUpgradeResult(true, targetLevel, reason, null);
    }
}
