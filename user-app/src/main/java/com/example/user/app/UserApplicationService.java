package com.example.user.app;

import com.example.user.api.UserServiceI;
import com.example.user.app.executor.UserRegisterCmdExe;
import com.example.user.app.executor.UserByIdQryExe;
import com.example.user.client.dto.Response;
import com.example.user.client.dto.cmd.UserRegisterCmd;
import com.example.user.client.dto.query.UserByIdQry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户应用服务
 * 应用层门面，协调各个执行器完成用例
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService implements UserServiceI {

    private final UserRegisterCmdExe userRegisterCmdExe;
    private final UserByIdQryExe userByIdQryExe;

    @Override
    public Response register(UserRegisterCmd cmd) {
        return userRegisterCmdExe.execute(cmd);
    }

    @Override
    public Response getById(UserByIdQry qry) {
        return userByIdQryExe.execute(qry);
    }
}
