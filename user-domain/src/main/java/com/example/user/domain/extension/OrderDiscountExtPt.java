package com.example.user.domain.extension;

import java.math.BigDecimal;

/**
 * 订单优惠计算扩展点接口
 * COLA架构：不同用户类型享受不同优惠
 */
public interface OrderDiscountExtPt extends ExtensionPointI {

    /**
     * 计算订单优惠
     *
     * @param userId     用户ID
     * @param amount     订单金额
     * @param productId  商品ID
     * @param bizScenario 业务场景
     * @return 优惠结果
     */
    DiscountResult calculateDiscount(Long userId, BigDecimal amount, String productId, BizScenario bizScenario);
}
