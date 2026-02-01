package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.DiscountResult;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.OrderDiscountExtPt;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * VIP用户订单优惠
 * 规则：8折 + 免运费
 */
@Slf4j
@Extension(bizId = "VIP", description = "VIP用户订单优惠")
public class VipOrderDiscountExt implements OrderDiscountExtPt {

    private static final BigDecimal SHIPPING_FEE = new BigDecimal("10");
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.2");  // 8折

    @Override
    public DiscountResult calculateDiscount(Long userId, BigDecimal amount, String productId, BizScenario bizScenario) {
        log.debug("计算VIP用户订单优惠: userId={}, amount={}", userId, amount);

        // 计算折扣金额（8折，优惠20%）
        BigDecimal discountAmount = amount.multiply(DISCOUNT_RATE);

        return new DiscountResult(
            discountAmount.add(SHIPPING_FEE),  // 折扣+免运费
            "VIP会员8折优惠+免运费",
            DiscountResult.DiscountType.PERCENT,
            DISCOUNT_RATE,
            SHIPPING_FEE,
            null,
            true
        );
    }
}
