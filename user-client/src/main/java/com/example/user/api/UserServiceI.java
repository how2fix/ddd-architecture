package com.example.user.api;

import com.example.user.client.dto.Response;
import com.example.user.client.dto.UserDTO;
import com.example.user.client.dto.cmd.UserRegisterCmd;
import com.example.user.client.dto.query.UserByIdQry;

/**
 * 用户服务接口
 * 对外API契约定义
 */
public interface UserServiceI {

    /**
     * 用户注册
     *
     * @param cmd 注册命令
     * @return 响应结果，用户ID在data中
     */
    Response register(UserRegisterCmd cmd);

    /**
     * 根据ID查询用户
     *
     * @param qry 查询条件
     * @return 用户信息，用户数据在data中
     */
    Response getById(UserByIdQry qry);
}
