package com.example.user.domain.extension;

import com.example.user.client.dto.cmd.UserRegisterCmd;

/**
 * 注册校验扩展点接口
 * COLA架构：不同注册方式有不同校验规则
 */
public interface RegisterValidatorExtPt extends ExtensionPointI {

    /**
     * 校验注册请求
     *
     * @param cmd         注册命令
     * @param bizScenario 业务场景
     * @throws BizException 校验失败时抛出业务异常
     */
    void validate(UserRegisterCmd cmd, BizScenario bizScenario);

    /**
     * 获取校验规则描述
     *
     * @param bizScenario 业务场景
     * @return 规则描述
     */
    default String getRuleDescription(BizScenario bizScenario) {
        return "注册校验规则";
    }
}
