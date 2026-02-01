package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.PointCalculateExtPt;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 每日签到积分
 */
@Slf4j
@Extension(bizId = "DEFAULT", useCase = "daily_checkin", description = "每日签到积分")
public class CheckinPointExt implements PointCalculateExtPt {

    @Override
    public Long calculate(Long userId, BigDecimal amount, BizScenario bizScenario) {
        // 每日签到+10积分
        log.debug("签到积分: userId={}", userId);
        return 10L;
    }

    @Override
    public String getRuleDescription(BizScenario bizScenario) {
        return "每日签到获得10积分";
    }
}
