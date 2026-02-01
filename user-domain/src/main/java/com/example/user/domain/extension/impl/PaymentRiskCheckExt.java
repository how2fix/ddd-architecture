package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.RiskCheckExtPt;
import com.example.user.domain.extension.RiskCheckResult;
import com.example.user.domain.extension.RiskContext;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支付场景风控检查
 */
@Slf4j
@Extension(bizId = "DEFAULT", useCase = "payment", description = "支付场景风控")
public class PaymentRiskCheckExt implements RiskCheckExtPt {

    // 模拟用户支付计数（最近1小时）
    private static final ConcurrentHashMap<Long, Integer> USER_PAYMENT_COUNT =
            new ConcurrentHashMap<>();

    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal("10000");
    private static final BigDecimal VERY_HIGH_AMOUNT_THRESHOLD = new BigDecimal("50000");

    @Override
    public RiskCheckResult check(Long userId, BigDecimal amount, RiskContext context) {
        log.debug("执行支付风控检查: userId={}, amount={}", userId, amount);

        int score = 0;

        // 检查支付金额
        if (amount.compareTo(VERY_HIGH_AMOUNT_THRESHOLD) > 0) {
            return RiskCheckResult.reject("单笔支付金额超过限制，请联系客服");
        }
        if (amount.compareTo(HIGH_AMOUNT_THRESHOLD) > 0) {
            score += 30;
        }

        // 检查支付频次
        Integer paymentCount = USER_PAYMENT_COUNT.getOrDefault(userId, 0);
        log.debug("用户最近支付次数: userId={}, count={}", userId, paymentCount);

        if (paymentCount >= 20) {
            return RiskCheckResult.reject("支付过于频繁，请1小时后再试");
        }
        if (paymentCount >= 10) {
            score += 40;
        } else if (paymentCount >= 5) {
            score += 20;
        }

        // 检查异地支付（模拟）
        if (isAbnormalLocation(userId, context.getIp())) {
            score += 30;
        }

        // 检查设备（模拟）
        if (isNewDevice(userId, context.getDeviceId())) {
            score += 10;
        }

        // 根据分数返回结果
        if (score >= 60) {
            return RiskCheckResult.warn("大额支付需要二次验证，请输入短信验证码");
        }
        if (score >= 40) {
            return RiskCheckResult.warn("检测到异常支付行为，请验证身份");
        }

        return RiskCheckResult.pass();
    }

    /**
     * 增加用户支付计数
     */
    public static void incrementPaymentCount(Long userId) {
        USER_PAYMENT_COUNT.merge(userId, 1, Integer::sum);
    }

    private boolean isAbnormalLocation(Long userId, String ip) {
        // 模拟异地支付检查
        return false;
    }

    private boolean isNewDevice(Long userId, String deviceId) {
        // 模拟新设备检查
        return false;
    }
}
