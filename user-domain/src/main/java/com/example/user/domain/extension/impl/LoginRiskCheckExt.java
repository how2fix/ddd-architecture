package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.RiskCheckExtPt;
import com.example.user.domain.extension.RiskCheckResult;
import com.example.user.domain.extension.RiskContext;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 登录场景风控检查
 */
@Slf4j
@Extension(bizId = "DEFAULT", useCase = "login", description = "登录场景风控")
public class LoginRiskCheckExt implements RiskCheckExtPt {

    // 模拟服务类
    private int passwordErrorCount = 0;
    private String lastLoginIp = null;

    @Override
    public RiskCheckResult check(Long userId, BigDecimal amount, RiskContext context) {
        log.debug("执行登录风控检查: userId={}, ip={}", userId, context.getIp());

        int score = 0;

        // 检查密码错误次数（模拟）
        if (passwordErrorCount >= 5) {
            return RiskCheckResult.reject("密码错误次数过多，账户已锁定30分钟");
        }
        if (passwordErrorCount >= 3) {
            score += 30;
        }

        // 检查异地登录（模拟）
        if (lastLoginIp != null && !lastLoginIp.equals(context.getIp())) {
            score += 20;
            log.info("检测到异地登录: userId={}, lastIp={}, currentIp={}",
                    userId, lastLoginIp, context.getIp());
        }

        // 检查IP是否在黑名单（模拟）
        if (isBlacklistedIp(context.getIp())) {
            return RiskCheckResult.reject("IP地址异常，请联系客服");
        }

        // 根据分数返回结果
        if (score >= 60) {
            return RiskCheckResult.warn("检测到异常登录，请验证身份");
        }
        if (score >= 30) {
            return RiskCheckResult.warn("密码错误次数较多，请输入验证码");
        }

        return RiskCheckResult.pass();
    }

    private boolean isBlacklistedIp(String ip) {
        // 模拟IP黑名单检查
        return false;
    }
}
