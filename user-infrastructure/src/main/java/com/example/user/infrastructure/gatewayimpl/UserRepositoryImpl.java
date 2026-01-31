package com.example.user.infrastructure.gatewayimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.user.domain.gateway.IUserRepository;
import com.example.user.domain.model.User;
import com.example.user.infrastructure.converter.UserConverter;
import com.example.user.infrastructure.dataobject.UserPO;
import com.example.user.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 用户仓储实现
 * 实现Domain层定义的IUserRepository接口
 * 依赖倒置原则：Infrastructure实现Domain的接口
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements IUserRepository {

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    @Override
    public User save(User user) {
        UserPO po = userConverter.toDataObject(user);
        userMapper.insert(po);
        user.setId(po.getId());
        return user;
    }

    @Override
    public User findById(Long id) {
        UserPO po = userMapper.selectById(id);
        return userConverter.toDomain(po);
    }

    @Override
    public User findByEmail(String email) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getEmail, email);
        UserPO po = userMapper.selectOne(wrapper);
        return userConverter.toDomain(po);
    }

    @Override
    public User findByPhone(String phone) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getPhone, phone);
        UserPO po = userMapper.selectOne(wrapper);
        return userConverter.toDomain(po);
    }

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getUsername, username);
        UserPO po = userMapper.selectOne(wrapper);
        return userConverter.toDomain(po);
    }

    @Override
    public User update(User user) {
        UserPO po = userConverter.toDataObject(user);
        userMapper.updateById(po);
        return user;
    }

    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
    }
}
