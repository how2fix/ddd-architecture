package com.example.user.domain.extension;

/**
 * 风控检查结果
 *
 * JDK 21 Feature: Record 类 - 不可变数据载体
 * - 替代 Lombok @Data + @Builder
 * - 内部枚举保持不变
 */
public record RiskCheckResult(
    boolean passed,
    RiskLevel riskLevel,
    String message,
    Integer score,
    RiskAction action
) {

    /**
     * 风控等级
     */
    public enum RiskLevel {
        SAFE,   // 安全
        LOW,    // 低风险
        MEDIUM, // 中风险
        HIGH,   // 高风险
        REJECT  // 拒绝
    }

    /**
     * 风控动作
     */
    public enum RiskAction {
        ALLOW,    // 允许通过
        CHALLENGE, // 需要额外验证（如短信验证码）
        BLOCK     // 阻止
    }

    /**
     * 创建通过结果
     */
    public static RiskCheckResult pass() {
        return new RiskCheckResult(true, RiskLevel.SAFE, "风控检查通过", 0, RiskAction.ALLOW);
    }

    /**
     * 创建警告结果（需要验证）
     */
    public static RiskCheckResult warn(String message) {
        return new RiskCheckResult(false, RiskLevel.MEDIUM, message, 50, RiskAction.CHALLENGE);
    }

    /**
     * 创建拒绝结果
     */
    public static RiskCheckResult reject(String message) {
        return new RiskCheckResult(false, RiskLevel.REJECT, message, 100, RiskAction.BLOCK);
    }

    /**
     * 是否需要额外验证
     */
    public boolean needChallenge() {
        return action == RiskAction.CHALLENGE;
    }

    /**
     * 是否被阻止
     */
    public boolean isBlocked() {
        return action == RiskAction.BLOCK;
    }
}
