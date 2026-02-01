package com.example.user.domain.extension.impl;

import com.example.user.client.dto.cmd.UserRegisterCmd;
import com.example.user.client.exception.BizException;
import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.RegisterValidatorExtPt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 微信注册校验
 * 场景：userClient=DEFAULT, useCase=register, scenario=wechat
 */
@Slf4j
@Extension(bizId = "DEFAULT", useCase = "register", scenario = "wechat", description = "微信注册校验")
public class WechatRegisterValidatorExt implements RegisterValidatorExtPt {

    @Override
    public void validate(UserRegisterCmd cmd, BizScenario bizScenario) {
        log.debug("执行微信注册校验: openId={}", cmd.getWechatOpenId());

        // 微信授权校验
        if (StringUtils.isEmpty(cmd.getWechatOpenId())) {
            throw BizException.of("10026", "微信授权失败，请重新授权");
        }

        // 微信unionid校验（可选）
        if (StringUtils.isEmpty(cmd.getWechatUnionId())) {
            log.warn("微信用户缺少unionId，可能影响跨应用登录");
        }

        // 检查openid是否已注册
        if (isWechatAlreadyRegistered(cmd.getWechatOpenId())) {
            throw BizException.of("10027", "该微信账号已绑定其他账号");
        }

        // 微信昵称校验
        if (StringUtils.isEmpty(cmd.getWechatNickname())) {
            throw BizException.of("10028", "无法获取微信昵称");
        }
    }

    @Override
    public String getRuleDescription(BizScenario bizScenario) {
        return "需要微信授权，检查openid是否已注册";
    }

    private boolean isWechatAlreadyRegistered(String openId) {
        // 模拟检查openid是否已注册
        // 实际应该查询数据库
        return false;
    }
}
