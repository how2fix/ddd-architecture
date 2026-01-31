package com.example.user.app.assembler;

import com.example.user.client.constant.UserStatus;
import com.example.user.client.dto.UserDTO;
import com.example.user.domain.model.User;
import org.springframework.stereotype.Component;

/**
 * 用户组装器
 * 负责DTO与领域对象之间的转换
 */
@Component
public class UserAssembler {

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
            .phone(user.getPhone() != null ? user.getPhone().getValue() : null)
            .status(user.getStatus() != null ? user.getStatus().name() : null)
            .registerTime(user.getRegisterTime())
            .lastActiveTime(user.getLastActiveTime())
            .build();
    }

    /**
     * DTO转领域对象
     * 注意：通常不会从DTO转换回领域对象，这里仅作示例
     */
    public User toDomain(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(new com.example.user.domain.model.Email(dto.getEmail()));
        user.setPhone(new com.example.user.domain.model.Phone(dto.getPhone()));
        user.setStatus(dto.getStatus() != null ? UserStatus.valueOf(dto.getStatus()) : null);
        user.setRegisterTime(dto.getRegisterTime());
        user.setLastActiveTime(dto.getLastActiveTime());
        return user;
    }
}
