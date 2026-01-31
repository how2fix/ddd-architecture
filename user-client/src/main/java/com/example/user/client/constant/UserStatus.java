package com.example.user.client.constant;

/**
 * 用户状态枚举
 */
public enum UserStatus {

    /**
     * 未激活
     */
    INACTIVE("未激活"),

    /**
     * 正常
     */
    ACTIVE("正常"),

    /**
     * 冻结
     */
    FROZEN("冻结");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
