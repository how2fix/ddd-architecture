package com.example.user.domain.extension;

import java.math.BigDecimal;

/**
 * 用户注册奖金扩展点接口
 * COLA架构：不同业务场景发放不同注册奖金
 */
public interface UserRegisterBonusExtPt extends ExtensionPointI {

    /**
     * 计算注册奖金
     *
     * @param userId       用户ID
     * @param bizScenario  业务场景
     * @return 奖金金额
     */
    BigDecimal calculateBonus(Long userId, BizScenario bizScenario);
}
