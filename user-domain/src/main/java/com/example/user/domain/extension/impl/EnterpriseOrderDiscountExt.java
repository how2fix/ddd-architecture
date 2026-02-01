package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.DiscountResult;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.OrderDiscountExtPt;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 企业用户订单优惠
 * 规则：7折 + 月结账期
 */
@Slf4j
@Extension(bizId = "ENTERPRISE", description = "企业用户订单优惠")
public class EnterpriseOrderDiscountExt implements OrderDiscountExtPt {

    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.3");  // 7折

    @Override
    public DiscountResult calculateDiscount(Long userId, BigDecimal amount, String productId, BizScenario bizScenario) {
        log.debug("计算企业用户订单优惠: userId={}, amount={}", userId, amount);

        // 计算折扣金额（7折，优惠30%）
        BigDecimal discountAmount = amount.multiply(DISCOUNT_RATE);

        return new DiscountResult(
            discountAmount,
            "企业用户7折优惠，支持月结账期",
            DiscountResult.DiscountType.PERCENT,
            DISCOUNT_RATE,
            null,
            null,
            false
        );
    }
}
