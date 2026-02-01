package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.PointCalculateExtPt;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 连续签到积分奖励
 */
@Slf4j
@Extension(bizId = "DEFAULT", useCase = "continuous_checkin", description = "连续签到积分奖励")
public class ContinuousCheckinPointExt implements PointCalculateExtPt {

    @Override
    public Long calculate(Long userId, BigDecimal days, BizScenario bizScenario) {
        int continuousDays = days.intValue();

        // 连续签到奖励
        long bonusPoints = switch (continuousDays) {
            case 3 -> 20L;      // 连续3天额外奖励20积分
            case 7 -> 50L;      // 连续7天额外奖励50积分
            case 15 -> 100L;    // 连续15天额外奖励100积分
            case 30 -> 300L;    // 连续30天额外奖励300积分
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
