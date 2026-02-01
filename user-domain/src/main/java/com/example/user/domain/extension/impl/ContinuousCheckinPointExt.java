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
        long bonusPoints;
        switch (continuousDays) {
            case 3:
                bonusPoints = 20L;
                break;
            case 7:
                bonusPoints = 50L;
                break;
            case 15:
                bonusPoints = 100L;
                break;
            case 30:
                bonusPoints = 300L;
                break;
            default:
                bonusPoints = 0L;
                break;
        }

        log.debug("连续签到奖励: userId={}, days={}, bonus={}", userId, continuousDays, bonusPoints);
        return bonusPoints;
    }

    @Override
    public String getRuleDescription(BizScenario bizScenario) {
        return "连续3天+20分，7天+50分，15天+100分，30天+300分";
    }
}
