package com.example.user.domain.extension;

import com.example.user.domain.model.User;

/**
 * 用户校验扩展点接口
 * COLA架构：不同业务场景可以有不同校验规则
 */
public interface UserValidatorExtPt extends ExtensionPointI {

    /**
     * 校验用户
     *
     * @param user         用户对象
     * @param bizScenario  业务场景
     * @throws BizException 校验失败时抛出业务异常
     */
    void validate(User user, BizScenario bizScenario);
}
