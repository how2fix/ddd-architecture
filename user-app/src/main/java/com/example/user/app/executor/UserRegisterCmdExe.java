package com.example.user.app.executor;

import com.example.user.client.constant.ErrorCode;
import com.example.user.client.dto.Response;
import com.example.user.client.dto.cmd.UserRegisterCmd;
import com.example.user.domain.gateway.IEmailGateway;
import com.example.user.domain.gateway.ISmsGateway;
import com.example.user.domain.gateway.IUserRepository;
import com.example.user.domain.model.User;
import com.example.user.domain.model.Email;
import com.example.user.domain.model.Phone;
import com.example.user.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户注册命令执行器
 * CQRS: Command执行器处理写操作
 */
@Component
@RequiredArgsConstructor
public class UserRegisterCmdExe {

    private static final Logger logger = LoggerFactory.getLogger(UserRegisterCmdExe.class);

    private final IUserRepository userRepository;
    private final IEmailGateway emailGateway;
    private final ISmsGateway smsGateway;
    private final UserDomainService userDomainService;

    /**
     * 执行用户注册
     * 事务边界在应用层控制
     */
    @Transactional(rollbackFor = Exception.class)
    public Response execute(UserRegisterCmd cmd) {
        // 1. 业务规则校验
        Response validation = validate(cmd);
        if (validation != null && !validation.isSuccess()) {
            return validation;
        }

        // 2. 创建领域对象
        User user = buildUser(cmd);

        // 3. 执行注册业务逻辑
        user.register(userDomainService.encryptPassword(cmd.getPassword()));

        // 4. 保存聚合根
        User savedUser = userRepository.save(user);

        // 5. 发送通知（异步处理）
        sendNotifications(cmd, savedUser);

        logger.info("用户注册成功, userId: {}, username: {}", savedUser.getId(), savedUser.getUsername());

        return Response.buildSuccess(savedUser.getId().toString());
    }

    /**
     * 业务规则校验
     * 返回null表示校验通过，否则返回错误响应
     */
    private Response validate(UserRegisterCmd cmd) {
        // 校验邮箱格式
        if (!Email.isValid(cmd.getEmail())) {
            return Response.buildFailure(
                ErrorCode.PARAM_INVALID.getCode(),
                "邮箱格式不正确"
            );
        }

        // 校验手机号格式
        if (!Phone.isValid(cmd.getPhone())) {
            return Response.buildFailure(
                ErrorCode.PARAM_INVALID.getCode(),
                "手机号格式不正确"
            );
        }

        // 校验邮箱是否已存在
        if (userDomainService.isEmailExists(cmd.getEmail())) {
            return Response.buildFailure(
                ErrorCode.EMAIL_ALREADY_EXISTS.getCode(),
                ErrorCode.EMAIL_ALREADY_EXISTS.getMessage()
            );
        }

        // 校验手机号是否已存在
        if (userDomainService.isPhoneExists(cmd.getPhone())) {
            return Response.buildFailure(
                ErrorCode.PHONE_ALREADY_EXISTS.getCode(),
                ErrorCode.PHONE_ALREADY_EXISTS.getMessage()
            );
        }

        // 校验用户名是否已存在
        if (userDomainService.isUsernameExists(cmd.getUsername())) {
            return Response.buildFailure(
                ErrorCode.USER_ALREADY_EXISTS.getCode(),
                ErrorCode.USER_ALREADY_EXISTS.getMessage()
            );
        }

        // 校验通过
        return null;
    }

    /**
     * 构建用户领域对象
     */
    private User buildUser(UserRegisterCmd cmd) {
        User user = new User();
        user.setUsername(cmd.getUsername());
        user.setEmail(new Email(cmd.getEmail()));
        user.setPhone(new Phone(cmd.getPhone()));
        return user;
    }

    /**
     * 发送通知
     * 注意：邮件和短信发送失败不影响注册流程
     */
    private void sendNotifications(UserRegisterCmd cmd, User user) {
        try {
            if (Boolean.TRUE.equals(cmd.getSendEmail())) {
                boolean emailSent = emailGateway.sendWelcomeEmail(
                    user.getEmail().getValue(),
                    user.getUsername()
                );
                if (!emailSent) {
                    logger.warn("欢迎邮件发送失败, email: {}", user.getEmail().getValue());
                }
            }
        } catch (Exception e) {
            logger.error("发送邮件异常", e);
        }

        try {
            if (Boolean.TRUE.equals(cmd.getSendSms())) {
                boolean smsSent = smsGateway.sendRegisterSms(
                    user.getPhone().getValue(),
                    user.getUsername()
                );
                if (!smsSent) {
                    logger.warn("注册短信发送失败, phone: {}", user.getPhone().getValue());
                }
            }
        } catch (Exception e) {
            logger.error("发送短信异常", e);
        }
    }
}
