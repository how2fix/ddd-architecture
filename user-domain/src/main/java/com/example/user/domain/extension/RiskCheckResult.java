package com.example.user.domain.extension;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 风控检查结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskCheckResult {

    /**
     * 是否通过
     */
    private boolean pass;

    /**
     * 风控等级：SAFE-安全, WARN-警告, REJECT-拒绝
     */
    private RiskLevel riskLevel;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 风控得分（0-100，分数越高风险越大）
     */
    private Integer score;

    /**
     * 风控动作：ALLOW-允许, CHALLENGE-挑战验证, BLOCK-阻止
     */
    private RiskAction action;

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
        return RiskCheckResult.builder()
                .pass(true)
                .riskLevel(RiskLevel.SAFE)
                .message("风控检查通过")
                .score(0)
                .action(RiskAction.ALLOW)
                .build();
    }

    /**
     * 创建警告结果（需要验证）
     */
    public static RiskCheckResult warn(String message) {
        return RiskCheckResult.builder()
                .pass(false)
                .riskLevel(RiskLevel.MEDIUM)
                .message(message)
                .score(50)
                .action(RiskAction.CHALLENGE)
                .build();
    }

    /**
     * 创建拒绝结果
     */
    public static RiskCheckResult reject(String message) {
        return RiskCheckResult.builder()
                .pass(false)
                .riskLevel(RiskLevel.REJECT)
                .message(message)
                .score(100)
                .action(RiskAction.BLOCK)
                .build();
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
