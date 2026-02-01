package com.example.user.domain.extension;

import java.util.Objects;

/**
 * 业务场景
 * COLA架构：用于标识不同的业务场景，支持扩展点路由
 *
 * 业务场景 = bizId + useCase + scenario
 * 例如：VIP.register.email
 * - bizId: VIP（VIP客户）
 * - useCase: register（注册场景）
 * - scenario: email（邮箱注册）
 */
public class BizScenario {

    /**
     * 业务身份（如：VIP, NORMAL, TMALL, TAOBAO）
     */
    private final String bizId;

    /**
     * 用例（如：register, login, order）
     */
    private final String useCase;

    /**
     * 场景（可选，如：email, phone, wechat）
     */
    private final String scenario;

    private BizScenario(String bizId, String useCase, String scenario) {
        this.bizId = bizId;
        this.useCase = useCase;
        this.scenario = scenario;
    }

    /**
     * 创建业务场景
     */
    public static BizScenario valueOf(String bizId, String useCase, String scenario) {
        return new BizScenario(bizId, useCase, scenario);
    }

    /**
     * 创建业务场景（无场景）
     */
    public static BizScenario valueOf(String bizId, String useCase) {
        return new BizScenario(bizId, useCase, null);
    }

    /**
     * 创建默认业务场景
     */
    public static BizScenario defaultScenario() {
        return new BizScenario("DEFAULT", null, null);
    }

    /**
     * 仅指定业务身份
     */
    public static BizScenario of(String bizId) {
        return new BizScenario(bizId, null, null);
    }

    /**
     * 获取唯一标识
     */
    public String getIdentity() {
        StringBuilder sb = new StringBuilder();
        if (bizId != null) {
            sb.append(bizId);
        }
        if (useCase != null) {
            sb.append(".").append(useCase);
        }
        if (scenario != null) {
            sb.append(".").append(scenario);
        }
        return sb.toString();
    }

    /**
     * 是否匹配指定的业务身份
     */
    public boolean matches(String targetBizId, String targetUseCase, String targetScenario) {
        boolean bizMatch = targetBizId == null ||
                "DEFAULT".equals(targetBizId) ||
                targetBizId.equals(this.bizId);
        boolean useCaseMatch = targetUseCase == null || targetUseCase.equals(this.useCase);
        boolean scenarioMatch = targetScenario == null || targetScenario.equals(this.scenario);
        return bizMatch && useCaseMatch && scenarioMatch;
    }

    public String getBizId() {
        return bizId;
    }

    public String getUseCase() {
        return useCase;
    }

    public String getScenario() {
        return scenario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BizScenario that = (BizScenario) o;
        return Objects.equals(bizId, that.bizId) &&
                Objects.equals(useCase, that.useCase) &&
                Objects.equals(scenario, that.scenario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bizId, useCase, scenario);
    }

    @Override
    public String toString() {
        return getIdentity();
    }
}
