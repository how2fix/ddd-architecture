package com.example.user.domain.extension;

import java.math.BigDecimal;

/**
 * 订单优惠结果
 *
 * JDK 21 Feature: Record 类 + Pattern Matching for switch
 * - 不可变数据载体
 * - 使用 when 子句进行条件守卫
 */
public record DiscountResult(
    BigDecimal discountAmount,
    String description,
    DiscountType discountType,
    BigDecimal discountRate,
    BigDecimal freeShippingAmount,
    String couponId,
    boolean freeShipping
) {

    /**
     * 优惠类型：PERCENT-百分比, AMOUNT-固定金额, SHIPPING-运费
     */
    public enum DiscountType {
        PERCENT,   // 百分比折扣
        AMOUNT,    // 固定金额减免
        SHIPPING,  // 运费优惠
        COUPON     // 优惠券
    }

    /**
     * 创建百分比折扣结果
     */
    public static DiscountResult percentDiscount(BigDecimal rate, String description) {
        return new DiscountResult(null, description, DiscountType.PERCENT, rate, null, null, false);
    }

    /**
     * 创建固定金额减免结果
     */
    public static DiscountResult amountDiscount(BigDecimal amount, String description) {
        return new DiscountResult(amount, description, DiscountType.AMOUNT, null, null, null, false);
    }

    /**
     * 创建免运费结果
     */
    public static DiscountResult freeShipping(BigDecimal shippingFee, String description) {
        return new DiscountResult(shippingFee, description, DiscountType.SHIPPING, null, shippingFee, null, true);
    }

    /**
     * 计算最终优惠金额（需要原始订单金额）
     *
     * JDK 21 Feature: Pattern matching for switch with when guards
     */
    public BigDecimal calculateFinalDiscount(BigDecimal originalAmount) {
        return switch (discountType) {
            case PERCENT -> originalAmount.multiply(discountRate);
            case AMOUNT, SHIPPING, COUPON -> discountAmount != null ? discountAmount : BigDecimal.ZERO;
        };
    }
}
