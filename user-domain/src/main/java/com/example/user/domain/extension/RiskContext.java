package com.example.user.domain.extension;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 风控上下文
 * 包含风控检查所需的各种上下文信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskContext {

    /**
     * 客户端IP地址
     */
    private String ip;

    /**
     * 设备指纹/设备ID
     */
    private String deviceId;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 场景类型（登录、注册、支付等）
     */
    private String sceneType;

    /**
     * 额外信息
     */
    private String extra;

    /**
     * 创建登录上下文
     */
    public static RiskContext forLogin(String ip, String deviceId) {
        return RiskContext.builder()
                .ip(ip)
                .deviceId(deviceId)
                .sceneType("LOGIN")
                .build();
    }

    /**
     * 创建注册上下文
     */
    public static RiskContext forRegister(String ip, String deviceId) {
        return RiskContext.builder()
                .ip(ip)
                .deviceId(deviceId)
                .sceneType("REGISTER")
                .build();
    }

    /**
     * 创建支付上下文
     */
    public static RiskContext forPayment(String ip, String deviceId, BigDecimal amount) {
        return RiskContext.builder()
                .ip(ip)
                .deviceId(deviceId)
                .sceneType("PAYMENT")
                .extra(amount.toString())
                .build();
    }
}
