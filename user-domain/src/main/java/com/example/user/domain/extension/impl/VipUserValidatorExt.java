package com.example.user.domain.extension.impl;

import com.example.user.client.exception.BizException;
import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.UserValidatorExtPt;
import com.example.user.domain.model.User;
import lombok.extern.slf4j.Slf4j;

/**
 * VIP用户校验扩展点
 * COLA架构：@Extension(bizId = "VIP") 表示这是VIP用户的实现
 */
@Slf4j
@Extension(bizId = "VIP", description = "VIP用户校验规则")
public class VipUserValidatorExt implements UserValidatorExtPt {

    @Override
    public void validate(User user, BizScenario bizScenario) {
        log.debug("执行VIP用户校验, username={}", user.getUsername());

        // VIP用户有更严格的校验规则
        if (user.getUsername() == null || user.getUsername().length() < 5) {
            throw BizException.of("10010", "VIP用户名至少5个字符");
        }

        if (user.getEmail() == null) {
            throw new BizException("10008", "邮箱不能为空");
        }

        // VIP用户必须有手机号
        if (user.getPhone() == null) {
            throw new BizException("10011", "VIP用户必须绑定手机号");
        }

        // VIP用户额外校验：邮箱必须是企业邮箱
        String email = user.getEmail().getValue();
        if (!email.matches(".*@(company|corp|enterprise)\\.com$")) {
            throw new BizException("10012", "VIP用户必须使用企业邮箱注册");
        }
    }
}
