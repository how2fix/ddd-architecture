package com.example.user.app.assembler;

import com.example.user.client.constant.UserStatus;
import com.example.user.client.dto.UserDTO;
import com.example.user.client.dto.cmd.UserRegisterCmd;
import com.example.user.domain.model.Email;
import com.example.user.domain.model.Phone;
import com.example.user.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户组装器
 * COLA架构：统一处理DTO与领域对象之间的转换
 *
 * 职责：
 * 1. DTO → Domain 转换
 * 2. Domain → DTO 转换
 * 3. 集合转换
 * 4. 数据脱敏
 */
@Component
public class UserAssembler {

    // ==================== 单个对象转换 ====================

    /**
     * 领域对象转DTO
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail() != null ? user.getEmail().getValue() : null)
                .phone(user.getPhone() != null ? getMaskedPhone(user.getPhone().getValue()) : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .registerTime(user.getRegisterTime())
                .lastActiveTime(user.getLastActiveTime())
                .build();
    }

    /**
     * 领域对象转DTO（完整信息，不脱敏）
     * 用于管理员查看完整信息
     */
    public UserDTO toFullDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail() != null ? user.getEmail().getValue() : null)
                .phone(user.getPhone() != null ? user.getPhone().getValue() : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .registerTime(user.getRegisterTime())
                .lastActiveTime(user.getLastActiveTime())
                .build();
    }

    /**
     * 注册命令转领域对象
     * 注意：值对象会在构造时自动校验
     */
    public User toDomain(UserRegisterCmd cmd) {
        if (cmd == null) {
            return null;
        }

        User user = new User();
        user.setUsername(cmd.getUsername());
        user.setEmail(cmd.getEmail() != null ? new Email(cmd.getEmail()) : null);
        user.setPhone(cmd.getPhone() != null ? new Phone(cmd.getPhone()) : null);
        // status、registerTime 等在 register() 方法中设置
        return user;
    }

    // ==================== 集合转换 ====================

    /**
     * 领域对象列表转DTO列表
     */
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 领域对象列表转DTO列表（完整信息）
     */
    public List<UserDTO> toFullDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(this::toFullDTO)
                .collect(Collectors.toList());
    }

    // ==================== 数据脱敏 ====================

    /**
     * 手机号脱敏
     * 13800138000 → 138****8000
     */
    private String getMaskedPhone(String phone) {
        if (phone == null || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     * test@example.com → t***@example.com
     */
    public String getMaskedEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        if (parts[0].length() > 1) {
            return parts[0].charAt(0) + "***@" + parts[1];
        }
        return email;
    }

    // ==================== 状态转换 ====================

    /**
     * 字符串状态转枚举
     */
    public UserStatus toStatus(String statusStr) {
        if (statusStr == null) {
            return null;
        }
        try {
            return UserStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            return UserStatus.INACTIVE;
        }
    }

    /**
     * 枚举状态转字符串
     */
    public String fromStatus(UserStatus status) {
        return status != null ? status.name() : null;
    }
}
