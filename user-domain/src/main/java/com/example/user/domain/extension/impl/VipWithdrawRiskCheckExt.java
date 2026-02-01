package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.RiskCheckExtPt;
import com.example.user.domain.extension.RiskCheckResult;
import com.example.user.domain.extension.RiskContext;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 提现场景风控检查（VIP用户）
 */
@Slf4j
@Extension(bizId = "VIP", useCase = "withdraw", description = "提现场景风控-VIP用户")
public class VipWithdrawRiskCheckExt implements RiskCheckExtPt {

    // VIP用户单日限额50000元
    private static final BigDecimal DAILY_LIMIT = new BigDecimal("50000");
    private static final BigDecimal SINGLE_LIMIT = new BigDecimal("10000");

    @Override
    public RiskCheckResult check(Long userId, BigDecimal amount, RiskContext context) {
        log.debug("执行提现风控检查(VIP用户): userId={}, amount={}", userId, amount);

        // 检查单笔限额
        if (amount.compareTo(SINGLE_LIMIT) > 0) {
            return RiskCheckResult.reject(
                    String.format("VIP用户单笔提现限额%s元", SINGLE_LIMIT));
        }

        // 检查单日限额
        BigDecimal withdrawnToday = getWithdrawnToday(userId);
        BigDecimal totalToday = withdrawnToday.add(amount);

        if (totalToday.compareTo(DAILY_LIMIT) > 0) {
            BigDecimal remaining = DAILY_LIMIT.subtract(withdrawnToday);
            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                return RiskCheckResult.reject(
                        String.format("今日还可提现%s元，超出限额", remaining));
            }
            return RiskCheckResult.reject("今日提现额度已用完");
        }

        // VIP用户大额提现额度更高
        if (amount.compareTo(new BigDecimal("5000")) > 0) {
            return RiskCheckResult.warn("大额提现需要验证，请输入短信验证码");
        }

        return RiskCheckResult.pass();
    }

    private BigDecimal getWithdrawnToday(Long userId) {
        // 模拟获取今日已提现金额
        return BigDecimal.ZERO;
    }
}
