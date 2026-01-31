package com.example.user.domain.gateway;

import com.example.user.domain.model.User;

/**
 * 用户仓储接口
 * 在Domain层定义，由Infrastructure层实现
 * 依赖倒置原则：Domain层定义接口，Infrastructure层实现
 */
public interface IUserRepository {

    /**
     * 保存用户
     */
    User save(User user);

    /**
     * 根据ID查找用户
     */
    User findById(Long id);

    /**
     * 根据邮箱查找用户
     */
    User findByEmail(String email);

    /**
     * 根据手机号查找用户
     */
    User findByPhone(String phone);

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 更新用户
     */
    User update(User user);

    /**
     * 删除用户
     */
    void delete(Long id);
}
