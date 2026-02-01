package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.PointCalculateExtPt;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 购物积分计算（普通用户）
 * 规则：1元 = 1积分
 */
@Slf4j
@Extension(bizId = "DEFAULT", useCase = "purchase", description = "购物积分-普通用户")
public class PurchasePointExt implements PointCalculateExtPt {

    @Override
    public Long calculate(Long userId, BigDecimal amount, BizScenario bizScenario) {
        // 1元 = 1积分
        long points = amount.longValue();
        log.debug("购物积分计算(普通): userId={}, amount={}, points={}", userId, amount, points);
        return points;
    }

    @Override
    public String getRuleDescription(BizScenario bizScenario) {
        return "购物1元获得1积分";
    }
}
