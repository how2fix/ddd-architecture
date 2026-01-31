package com.example.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.dataobject.UserPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 * MyBatis Plus基础Mapper，提供CRUD操作
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
