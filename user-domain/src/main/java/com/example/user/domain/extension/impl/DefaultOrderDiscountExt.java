package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.DiscountResult;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.OrderDiscountExtPt;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 普通用户订单优惠
 * 规则：满100减10，满200减25，满300减40
 */
@Slf4j
@Extension(bizId = "DEFAULT", description = "普通用户订单优惠")
public class DefaultOrderDiscountExt implements OrderDiscountExtPt {

    private static final BigDecimal THRESHOLD_1 = new BigDecimal("100");
    private static final BigDecimal THRESHOLD_2 = new BigDecimal("200");
    private static final BigDecimal THRESHOLD_3 = new BigDecimal("300");

    @Override
    public DiscountResult calculateDiscount(Long userId, BigDecimal amount, String productId, BizScenario bizScenario) {
        log.debug("计算普通用户订单优惠: userId={}, amount={}", userId, amount);

        BigDecimal discount = BigDecimal.ZERO;
        String description = "暂无优惠";

        if (amount.compareTo(THRESHOLD_3) >= 0) {
            discount = new BigDecimal("40");
            description = "满300减40";
        } else if (amount.compareTo(THRESHOLD_2) >= 0) {
            discount = new BigDecimal("25");
            description = "满200减25";
        } else if (amount.compareTo(THRESHOLD_1) >= 0) {
            discount = new BigDecimal("10");
            description = "满100减10";
        }

        return new DiscountResult(
            discount,
            description,
            DiscountResult.DiscountType.AMOUNT,
            null,
            null,
            null,
            false
        );
    }
}
