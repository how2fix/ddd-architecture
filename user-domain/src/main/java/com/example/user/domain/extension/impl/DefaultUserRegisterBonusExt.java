package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.UserRegisterBonusExtPt;

import java.math.BigDecimal;

/**
 * 默认注册奖金扩展点
 */
@Extension(bizId = "DEFAULT", description = "默认用户注册奖金")
public class DefaultUserRegisterBonusExt implements UserRegisterBonusExtPt {

    @Override
    public BigDecimal calculateBonus(Long userId, BizScenario bizScenario) {
        // 默认用户无奖金
        return BigDecimal.ZERO;
    }
}
