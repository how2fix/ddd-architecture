package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.PointCalculateExtPt;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 购物积分计算（VIP用户）
 * 规则：1元 = 2积分（双倍）
 */
@Slf4j
@Extension(bizId = "VIP", useCase = "purchase", description = "购物积分-VIP用户")
public class VipPurchasePointExt implements PointCalculateExtPt {

    @Override
    public Long calculate(Long userId, BigDecimal amount, BizScenario bizScenario) {
        // VIP用户 1元 = 2积分
        long points = amount.multiply(new BigDecimal("2")).longValue();
        log.debug("购物积分计算(VIP): userId={}, amount={}, points={}", userId, amount, points);
        return points;
    }

    @Override
    public String getRuleDescription(BizScenario bizScenario) {
        return "VIP会员购物1元获得2积分";
    }
}
