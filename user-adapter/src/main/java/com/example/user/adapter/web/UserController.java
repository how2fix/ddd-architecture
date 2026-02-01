package com.example.user.adapter.web;

import com.example.user.api.UserServiceI;
import com.example.user.client.dto.Response;
import com.example.user.client.dto.UserDTO;
import com.example.user.client.dto.cmd.UserRegisterCmd;
import com.example.user.client.dto.query.UserByIdQry;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * Adapter层：处理HTTP请求，协议转换
 * 这是一个薄层，不包含业务逻辑，只做参数校验和路由
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserServiceI userService;

    /**
     * 用户注册
     * POST /api/users/register
     */
    @PostMapping("/register")
    public Response register(@Valid @RequestBody UserRegisterCmd cmd) {
        logger.info("收到用户注册请求, username: {}, email: {}", cmd.getUsername(), cmd.getEmail());
        return userService.register(cmd);
    }

    /**
     * 根据ID查询用户
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public Response getById(@PathVariable("id") Long id) {
        logger.info("收到查询用户请求, userId: {}", id);
        UserByIdQry qry = new UserByIdQry();
        qry.setUserId(id);
        return userService.getById(qry);
    }

    /**
     * 健康检查
     * GET /api/users/health
     */
    @GetMapping("/health")
    public Response health() {
        return Response.buildSuccess("OK");
    }
}
