package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.LevelUpgradeExtPt;
import com.example.user.domain.extension.LevelUpgradeResult;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 企业用户等级升级扩展点
 * 升级规则：需要人工审核，不自动升级
 */
@Slf4j
@Extension(bizId = "ENTERPRISE", description = "企业用户升级规则")
public class EnterpriseUserLevelUpgradeExt implements LevelUpgradeExtPt {

    @Override
    public LevelUpgradeResult checkUpgrade(
            Long userId,
            String currentLevel,
            BigDecimal totalConsumption,
            Long totalPoints,
            BizScenario bizScenario
    ) {
        log.debug("检查企业用户升级: userId={}, consumption={}", userId, totalConsumption);

        // 企业用户需要人工审核，不自动升级
        if (totalConsumption.compareTo(new BigDecimal("10000")) >= 0) {
            return LevelUpgradeResult.builder()
                    .canUpgrade(false)
                    .targetLevel("ENTERPRISE_VIP")
                    .reason("企业用户升级需要人工审核，请提交企业认证申请")
                    .build();
        }

        return LevelUpgradeResult.notMet("企业用户暂不支持自动升级");
    }
}
