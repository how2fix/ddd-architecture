package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.UserRegisterBonusExtPt;

import java.math.BigDecimal;

/**
 * VIP注册奖金扩展点
 */
@Extension(bizId = "VIP", description = "VIP用户注册奖金")
public class VipUserRegisterBonusExt implements UserRegisterBonusExtPt {

    @Override
    public BigDecimal calculateBonus(Long userId, BizScenario bizScenario) {
        // VIP用户注册送100元奖金
        return new BigDecimal("100");
    }
}
