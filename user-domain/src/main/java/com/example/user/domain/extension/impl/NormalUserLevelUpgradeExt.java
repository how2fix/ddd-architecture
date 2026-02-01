package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.LevelUpgradeExtPt;
import com.example.user.domain.extension.LevelUpgradeResult;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 普通用户等级升级扩展点
 * 升级规则：累计消费满1000元升级到VIP
 */
@Slf4j
@Extension(bizId = "NORMAL", description = "普通用户升级规则")
public class NormalUserLevelUpgradeExt implements LevelUpgradeExtPt {

    private static final BigDecimal UPGRADE_THRESHOLD = new BigDecimal("1000");

    @Override
    public LevelUpgradeResult checkUpgrade(
            Long userId,
            String currentLevel,
            BigDecimal totalConsumption,
            Long totalPoints,
            BizScenario bizScenario
    ) {
        log.debug("检查普通用户升级: userId={}, consumption={}", userId, totalConsumption);

        // 已经是VIP，检查是否可以升级到SVIP
        if ("VIP".equals(currentLevel)) {
            if (totalConsumption.compareTo(new BigDecimal("5000")) >= 0) {
                return LevelUpgradeResult.success("SVIP", "累计消费满5000元，升级到SVIP");
            }
            long required = 5000 - totalConsumption.longValue();
            return LevelUpgradeResult.notMet("SVIP", required);
        }

        // 普通用户升级到VIP
        if (totalConsumption.compareTo(UPGRADE_THRESHOLD) >= 0) {
            return LevelUpgradeResult.success("VIP",
                String.format("累计消费%.2f元，恭喜升级到VIP会员！", totalConsumption));
        }

        long required = UPGRADE_THRESHOLD.subtract(totalConsumption).longValue();
        return LevelUpgradeResult.notMet("VIP", required);
    }
}
