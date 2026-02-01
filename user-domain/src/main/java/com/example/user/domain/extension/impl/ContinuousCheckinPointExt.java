package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.PointCalculateExtPt;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 连续签到积分奖励
 *
 * JDK 21 Feature: switch 表达式 + 箭头语法
 */
@Slf4j
@Extension(bizId = "DEFAULT", useCase = "continuous_checkin", description = "连续签到积分奖励")
public class ContinuousCheckinPointExt implements PointCalculateExtPt {

    @Override
    public Long calculate(Long userId, BigDecimal days, BizScenario bizScenario) {
        int continuousDays = days.intValue();

        // JDK 21: switch 表达式
        long bonusPoints = switch (continuousDays) {
            case 3 -> 20L;
            case 7 -> 50L;
            case 15 -> 100L;
            case 30 -> 300L;
            default -> 0L;
        };

        log.debug("连续签到奖励: userId={}, days={}, bonus={}", userId, continuousDays, bonusPoints);
        return bonusPoints;
    }

    @Override
    public String getRuleDescription(BizScenario bizScenario) {
        return "连续3天+20分，7天+50分，15天+100分，30天+300分";
    }
}
