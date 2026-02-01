package com.example.user.domain.extension.impl;

import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.PointCalculateExtPt;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 评论积分奖励
 */
@Slf4j
@Extension(bizId = "DEFAULT", useCase = "comment", description = "评论积分")
public class CommentPointExt implements PointCalculateExtPt {

    @Override
    public Long calculate(Long userId, BigDecimal amount, BizScenario bizScenario) {
        // 评论+5积分
        log.debug("评论积分: userId={}", userId);
        return 5L;
    }

    @Override
    public String getRuleDescription(BizScenario bizScenario) {
        return "发表评论获得5积分";
    }
}
