package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.LevelUpgradeExtPt;
import com.example.user.domain.extension.LevelUpgradeResult;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * VIP用户等级升级扩展点
 * 升级规则：累计消费满5000元升级到SVIP
 */
@Slf4j
@Extension(bizId = "VIP", description = "VIP用户升级规则")
public class VipUserLevelUpgradeExt implements LevelUpgradeExtPt {

    private static final BigDecimal SVIP_THRESHOLD = new BigDecimal("5000");

    @Override
    public LevelUpgradeResult checkUpgrade(
            Long userId,
            String currentLevel,
            BigDecimal totalConsumption,
            Long totalPoints,
            BizScenario bizScenario
    ) {
        log.debug("检查VIP用户升级: userId={}, consumption={}", userId, totalConsumption);

        // VIP用户升级到SVIP
        if (totalConsumption.compareTo(SVIP_THRESHOLD) >= 0) {
            return LevelUpgradeResult.success("SVIP",
                String.format("尊贵的VIP会员，累计消费%.2f元，升级到SVIP尊享会员！", totalConsumption));
        }

        long required = SVIP_THRESHOLD.subtract(totalConsumption).longValue();
        return LevelUpgradeResult.notMet("SVIP", required);
    }
}
