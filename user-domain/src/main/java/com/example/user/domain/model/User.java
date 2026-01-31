package com.example.user.domain.model;

import com.example.user.client.constant.UserStatus;
import com.example.user.domain.service.UserDomainService;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户聚合根
 * 充血模型：业务逻辑封装在实体内部
 */
@Data
public class User {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱（值对象）
     */
    private Email email;

    /**
     * 手机号（值对象）
     */
    private Phone phone;

    /**
     * 密码（加密后）
     */
    private String password;

    /**
     * 用户状态
     */
    private UserStatus status;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 用户注册
     * 业务规则封装在聚合根内
     */
    public void register(String encryptedPassword) {
        this.status = UserStatus.INACTIVE;
        this.password = encryptedPassword;
        this.registerTime = LocalDateTime.now();
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 激活用户
     */
    public void activate() {
        if (this.status == UserStatus.FROZEN) {
            throw new IllegalStateException("冻结用户不能激活");
        }
        this.status = UserStatus.ACTIVE;
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 冻结用户
     */
    public void freeze() {
        this.status = UserStatus.FROZEN;
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 修改邮箱
     */
    public void changeEmail(String newEmail) {
        if (this.status == UserStatus.FROZEN) {
            throw new IllegalStateException("冻结用户不能修改邮箱");
        }
        this.email = new Email(newEmail);
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 更新最后活跃时间
     */
    public void updateLastActiveTime() {
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 校验密码
     */
    public boolean validatePassword(String inputPassword, UserDomainService domainService) {
        return domainService.verifyPassword(inputPassword, this.password);
    }

    /**
     * 是否可以激活
     */
    public boolean canActivate() {
        return this.status == UserStatus.INACTIVE;
    }

    /**
     * 是否冻结
     */
    public boolean isFrozen() {
        return this.status == UserStatus.FROZEN;
    }
}
