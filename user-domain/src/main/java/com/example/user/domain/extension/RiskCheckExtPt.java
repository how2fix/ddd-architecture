package com.example.user.domain.extension;

import java.math.BigDecimal;

/**
 * 风控校验扩展点接口
 * COLA架构：不同业务场景有不同风控规则
 */
public interface RiskCheckExtPt extends ExtensionPointI {

    /**
     * 风控检查
     *
     * @param userId  用户ID
     * @param amount  金额（可选）
     * @param context 风控上下文（IP、设备等）
     * @return 风控检查结果
     */
    RiskCheckResult check(Long userId, BigDecimal amount, RiskContext context);
}
