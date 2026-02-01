package com.example.user.domain.extension.impl;

import com.example.user.client.dto.cmd.UserRegisterCmd;
import com.example.user.client.exception.BizException;
import com.example.user.client.exception.ErrorCode;
import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.RegisterValidatorExtPt;
import com.example.user.domain.model.Phone;
import lombok.extern.slf4j.Slf4j;

/**
 * 手机号注册校验
 * 场景：userClient=DEFAULT, useCase=register, scenario=phone
 */
@Slf4j
@Extension(bizId = "DEFAULT", useCase = "register", scenario = "phone", description = "手机号注册校验")
public class PhoneRegisterValidatorExt implements RegisterValidatorExtPt {

    @Override
    public void validate(UserRegisterCmd cmd, BizScenario bizScenario) {
        log.debug("执行手机号注册校验: phone={}", cmd.getPhone());

        // 手机号格式校验
        if (!Phone.isValid(cmd.getPhone())) {
            throw ErrorCode.PARAM_INVALID.toException("手机号格式不正确");
        }

        // 短信验证码校验
        if (cmd.getVerifyCode() == null || cmd.getVerifyCode().isEmpty()) {
            throw BizException.of("10023", "请输入短信验证码");
        }

        // 模拟验证码校验
        if (!checkSmsCode(cmd.getPhone(), cmd.getVerifyCode())) {
            throw BizException.of("10024", "短信验证码错误或已过期");
        }

        // 手机号运营商校验（可选）
        if (!isValidCarrier(cmd.getPhone())) {
            throw BizException.of("10025", "暂不支持该运营商号码注册");
        }
    }

    @Override
    public String getRuleDescription(BizScenario bizScenario) {
        return "需要验证手机号，格式正确，支持主流运营商";
    }

    private boolean checkSmsCode(String phone, String code) {
        // 模拟短信验证码校验
        return "123456".equals(code);
    }

    private boolean isValidCarrier(String phone) {
        // 模拟运营商校验
        // 中国移动：134-139, 147, 150-152, 157-159, 178, 182-184, 187-188
        // 中国联通：130-132, 145, 155-156, 175-176, 185-186
        // 中国电信：133, 149, 153, 173-177, 180-181, 189
        String prefix = phone.substring(0, 3);
        return prefix.matches("1[34578]\\d");
    }
}
