package com.example.user.domain.extension;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单优惠结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountResult {

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 优惠说明
     */
    private String description;

    /**
     * 优惠类型：PERCENT-百分比, AMOUNT-固定金额, SHIPPING-运费
     */
    private DiscountType discountType;

    /**
     * 折扣率（百分比类型使用，如0.1表示9折）
     */
    private BigDecimal discountRate;

    /**
     * 免运费金额
     */
    private BigDecimal freeShippingAmount;

    /**
     * 优惠券ID
     */
    private String couponId;

    /**
     * 是否免运费
     */
    private boolean freeShipping;

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
        return DiscountResult.builder()
                .discountType(DiscountType.PERCENT)
                .discountRate(rate)
                .description(description)
                .build();
    }

    /**
     * 创建固定金额减免结果
     */
    public static DiscountResult amountDiscount(BigDecimal amount, String description) {
        return DiscountResult.builder()
                .discountType(DiscountType.AMOUNT)
                .discountAmount(amount)
                .description(description)
                .build();
    }

    /**
     * 创建免运费结果
     */
    public static DiscountResult freeShipping(BigDecimal shippingFee, String description) {
        return DiscountResult.builder()
                .discountType(DiscountType.SHIPPING)
                .discountAmount(shippingFee)
                .freeShipping(true)
                .freeShippingAmount(shippingFee)
                .description(description)
                .build();
    }

    /**
     * 计算最终优惠金额（需要原始订单金额）
     */
    public BigDecimal calculateFinalDiscount(BigDecimal originalAmount) {
        return switch (discountType) {
            case PERCENT -> originalAmount.multiply(discountRate);
            case AMOUNT, SHIPPING, COUPON -> discountAmount != null ? discountAmount : BigDecimal.ZERO;
        };
    }
}
