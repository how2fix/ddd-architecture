package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.RiskCheckExtPt;
import com.example.user.domain.extension.RiskCheckResult;
import com.example.user.domain.extension.RiskContext;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 注册场景风控检查
 */
@Slf4j
@Extension(bizId = "DEFAULT", useCase = "register", description = "注册场景风控")
public class RegisterRiskCheckExt implements RiskCheckExtPt {

    // 模拟IP注册计数
    private static final ConcurrentHashMap<String, AtomicInteger> IP_REGISTER_COUNT =
            new ConcurrentHashMap<>();

    // 模拟设备黑名单
    private static final String BLACKLISTED_DEVICE_PREFIX = "BLACK_";

    @Override
    public RiskCheckResult check(Long userId, BigDecimal amount, RiskContext context) {
        log.debug("执行注册风控检查: ip={}, deviceId={}", context.getIp(), context.getDeviceId());

        // 检查IP注册频次
        AtomicInteger count = IP_REGISTER_COUNT.computeIfAbsent(
                context.getIp(),
                k -> new AtomicInteger(0)
        );

        int currentCount = count.get();
        log.debug("IP注册计数: ip={}, count={}", context.getIp(), currentCount);

        if (currentCount >= 5) {
            return RiskCheckResult.reject("该IP注册过于频繁，请24小时后再试");
        }
        if (currentCount >= 3) {
            return RiskCheckResult.warn("该IP注册次数较多，请输入图形验证码");
        }

        // 检查设备指纹是否在黑名单
        if (context.getDeviceId() != null &&
            context.getDeviceId().startsWith(BLACKLISTED_DEVICE_PREFIX)) {
            return RiskCheckResult.reject("设备存在风险，请使用其他设备");
        }

        // 检查是否是代理IP（模拟）
        if (isProxyIp(context.getIp())) {
            return RiskCheckResult.warn("检测到代理IP，请验证身份");
        }

        return RiskCheckResult.pass();
    }

    /**
     * 模拟增加IP注册计数
     */
    public static void incrementIpCount(String ip) {
        IP_REGISTER_COUNT.computeIfAbsent(ip, k -> new AtomicInteger(0)).incrementAndGet();
    }

    private boolean isProxyIp(String ip) {
        // 模拟代理IP检查
        return false;
    }
}
