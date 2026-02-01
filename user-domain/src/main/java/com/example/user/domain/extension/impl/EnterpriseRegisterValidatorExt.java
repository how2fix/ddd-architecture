package com.example.user.domain.extension.impl;

import com.example.user.client.dto.cmd.UserRegisterCmd;
import com.example.user.client.exception.BizException;
import com.example.user.client.exception.ErrorCode;
import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.Extension;
import com.example.user.domain.extension.RegisterValidatorExtPt;
import com.example.user.domain.model.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 企业用户注册校验
 * 场景：userClient=ENTERPRISE, useCase=register
 */
@Slf4j
@Extension(bizId = "ENTERPRISE", useCase = "register", description = "企业用户注册校验")
public class EnterpriseRegisterValidatorExt implements RegisterValidatorExtPt {

    @Override
    public void validate(UserRegisterCmd cmd, BizScenario bizScenario) {
        log.debug("执行企业用户注册校验: email={}, company={}",
                cmd.getEmail(), cmd.getCompanyName());

        // 企业邮箱校验
        if (!Email.isValid(cmd.getEmail())) {
            throw ErrorCode.PARAM_INVALID.toException("邮箱格式不正确");
        }

        if (!isEnterpriseEmail(cmd.getEmail())) {
            throw BizException.of("10029", "请使用企业邮箱注册（支持@company.com, @corp.com, @enterprise.com）");
        }

        // 企业名称校验
        if (StringUtils.isEmpty(cmd.getCompanyName())) {
            throw BizException.of("10030", "请填写企业名称");
        }

        if (cmd.getCompanyName().length() < 3) {
            throw BizException.of("10031", "企业名称至少3个字符");
        }

        // 营业执照校验
        if (StringUtils.isEmpty(cmd.getBusinessLicense())) {
            throw BizException.of("10032", "请上传营业执照");
        }

        // 统一社会信用代码校验
        if (StringUtils.isEmpty(cmd.getCreditCode())) {
            throw BizException.of("10033", "请填写统一社会信用代码");
        }

        if (!isValidCreditCode(cmd.getCreditCode())) {
            throw BizException.of("10034", "统一社会信用代码格式不正确");
        }
    }

    @Override
    public String getRuleDescription(BizScenario bizScenario) {
        return "需要企业邮箱、企业名称、营业执照、统一社会信用代码";
    }

    private boolean isEnterpriseEmail(String email) {
        // 检查是否是企业邮箱
        String domain = email.substring(email.lastIndexOf("@") + 1);
        return domain.equals("company.com") ||
                domain.equals("corp.com") ||
                domain.equals("enterprise.com") ||
                domain.endsWith(".company.com") ||
                domain.endsWith(".corp.com");
    }

    private boolean isValidCreditCode(String creditCode) {
        // 统一社会信用代码：18位，由数字和大写字母组成
        if (creditCode == null || creditCode.length() != 18) {
            return false;
        }
        return creditCode.matches("[0-9A-HJ-NPQRTUWXY]{18}");
    }
}
