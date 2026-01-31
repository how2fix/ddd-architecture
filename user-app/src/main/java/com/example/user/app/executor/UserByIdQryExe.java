package com.example.user.app.executor;

import com.example.user.app.assembler.UserAssembler;
import com.example.user.client.constant.ErrorCode;
import com.example.user.client.dto.Response;
import com.example.user.client.dto.UserDTO;
import com.example.user.client.dto.query.UserByIdQry;
import com.example.user.domain.gateway.IUserRepository;
import com.example.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 根据ID查询用户执行器
 * CQRS: Query执行器处理读操作
 */
@Component
@RequiredArgsConstructor
public class UserByIdQryExe {

    private final IUserRepository userRepository;
    private final UserAssembler userAssembler;

    /**
     * 执行查询
     */
    public Response execute(UserByIdQry qry) {
        User user = userRepository.findById(qry.getUserId());

        if (user == null) {
            return Response.buildFailure(
                ErrorCode.USER_NOT_FOUND.getCode(),
                ErrorCode.USER_NOT_FOUND.getMessage()
            );
        }

        return Response.buildSuccess(userAssembler.toDTO(user));
    }
}
