package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.DiscountResult;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.OrderDiscountExtPt;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 新用户订单优惠
 * 规则：首单9折
 */
@Slf4j
@Extension(bizId = "NEW_USER", description = "新用户订单优惠")
public class NewUserOrderDiscountExt implements OrderDiscountExtPt {

    @Override
    public DiscountResult calculateDiscount(Long userId, BigDecimal amount, String productId, BizScenario bizScenario) {
        log.debug("计算新用户订单优惠: userId={}, amount={}", userId, amount);

        // 首单9折
        return new DiscountResult(
            amount.multiply(new BigDecimal("0.1")),
            "新用户首单9折优惠",
            DiscountResult.DiscountType.PERCENT,
            new BigDecimal("0.1"),
            null,
            null,
            false
        );
    }
}
