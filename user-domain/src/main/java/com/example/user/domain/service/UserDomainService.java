package com.example.user.domain.service;

import com.example.user.domain.gateway.IUserRepository;
import com.example.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户领域服务
 * 处理跨聚合根的业务逻辑或无状态的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final IUserRepository userRepository;

    /**
     * 检查邮箱是否已存在
     */
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    /**
     * 检查手机号是否已存在
     */
    public boolean isPhoneExists(String phone) {
        return userRepository.findByPhone(phone) != null;
    }

    /**
     * 检查用户名是否已存在
     */
    public boolean isUsernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    /**
     * 密码加密
     */
    public String encryptPassword(String plainPassword) {
        // 模拟密码加密（实际应使用BCrypt等）
        return "ENC_" + plainPassword;
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(String plainPassword, String encryptedPassword) {
        return encryptPassword(plainPassword).equals(encryptedPassword);
    }

    /**
     * 生成激活码
     */
    public String generateActivationCode() {
        return String.valueOf((int) ((Math.random() * 900000) + 100000));
    }
}
