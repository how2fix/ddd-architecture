package com.example.user.infrastructure.converter;

import com.example.user.client.constant.UserStatus;
import com.example.user.domain.model.Email;
import com.example.user.domain.model.Phone;
import com.example.user.domain.model.User;
import com.example.user.infrastructure.dataobject.UserPO;
import org.springframework.stereotype.Component;

/**
 * 用户转换器
 * 负责领域对象与持久化对象之间的转换
 */
@Component
public class UserConverter {

    /**
     * 领域对象转持久化对象
     */
    public UserPO toDataObject(User user) {
        if (user == null) {
            return null;
        }

        UserPO po = new UserPO();
        po.setId(user.getId());
        po.setUsername(user.getUsername());
        po.setEmail(user.getEmail() != null ? user.getEmail().getValue() : null);
        po.setPhone(user.getPhone() != null ? user.getPhone().getValue() : null);
        po.setPassword(user.getPassword());
        po.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        po.setRegisterTime(user.getRegisterTime());
        po.setLastActiveTime(user.getLastActiveTime());
        return po;
    }

    /**
     * 持久化对象转领域对象
     */
    public User toDomain(UserPO po) {
        if (po == null) {
            return null;
        }

        User user = new User();
        user.setId(po.getId());
        user.setUsername(po.getUsername());
        user.setEmail(po.getEmail() != null ? new Email(po.getEmail()) : null);
        user.setPhone(po.getPhone() != null ? new Phone(po.getPhone()) : null);
        user.setPassword(po.getPassword());
        user.setStatus(po.getStatus() != null ? UserStatus.valueOf(po.getStatus()) : null);
        user.setRegisterTime(po.getRegisterTime());
        user.setLastActiveTime(po.getLastActiveTime());
        return user;
    }
}
