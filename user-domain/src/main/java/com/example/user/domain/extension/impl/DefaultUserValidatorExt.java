package com.example.user.domain.extension.impl;

import com.example.user.client.exception.BizException;
import com.example.user.client.exception.ErrorCode;
import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.UserValidatorExtPt;
import com.example.user.domain.model.User;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认用户校验扩展点
 * COLA架构：@Extension(bizId = "DEFAULT") 表示这是默认实现
 */
@Slf4j
@Extension(bizId = "DEFAULT", description = "默认用户校验规则")
public class DefaultUserValidatorExt implements UserValidatorExtPt {

    @Override
    public void validate(User user, BizScenario bizScenario) {
        log.debug("执行默认用户校验, username={}", user.getUsername());

        // 默认校验规则
        if (user.getUsername() == null || user.getUsername().length() < 3) {
            throw BizException.of(ErrorCode.PARAM_INVALID);
        }

        if (user.getEmail() == null) {
            throw new BizException("10008", "邮箱不能为空");
        }

        if (user.getPhone() == null) {
            throw new BizException("10009", "手机号不能为空");
        }
    }
}
