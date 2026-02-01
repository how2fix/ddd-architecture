package com.example.user.domain.extension.impl;

import com.example.user.client.dto.cmd.UserRegisterCmd;
import com.example.user.client.exception.BizException;
import com.example.user.client.exception.ErrorCode;
import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.RegisterValidatorExtPt;
import com.example.user.domain.model.Email;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮箱注册校验
 * 场景：userClient=DEFAULT, useCase=register, scenario=email
 */
@Slf4j
@Extension(bizId = "DEFAULT", useCase = "register", scenario = "email", description = "邮箱注册校验")
public class EmailRegisterValidatorExt implements RegisterValidatorExtPt {

    @Override
    public void validate(UserRegisterCmd cmd, BizScenario bizScenario) {
        log.debug("执行邮箱注册校验: email={}", cmd.getEmail());

        // 邮箱格式校验
        if (!Email.isValid(cmd.getEmail())) {
            throw ErrorCode.PARAM_INVALID.toException("邮箱格式不正确");
        }

        // 邮箱验证码校验
        if (cmd.getVerifyCode() == null || cmd.getVerifyCode().isEmpty()) {
            throw BizException.of("10020", "请输入邮箱验证码");
        }

        // 模拟验证码校验
        if (!checkEmailCode(cmd.getEmail(), cmd.getVerifyCode())) {
            throw BizException.of("10021", "邮箱验证码错误或已过期");
        }

        // 邮箱域名校验（可选）
        String domain = cmd.getEmail().substring(cmd.getEmail().lastIndexOf("@") + 1);
        if (isDisposableEmail(domain)) {
            throw BizException.of("10022", "不支持临时邮箱注册");
        }
    }

    @Override
    public String getRuleDescription(BizScenario bizScenario) {
        return "需要验证邮箱，格式正确，非临时邮箱";
    }

    private boolean checkEmailCode(String email, String code) {
        // 模拟邮箱验证码校验
        // 实际应该从缓存或数据库中验证
        return "123456".equals(code);
    }

    private boolean isDisposableEmail(String domain) {
        // 模拟临时邮箱检测
        return domain.startsWith("temp.") || domain.startsWith("throwaway.");
    }
}
