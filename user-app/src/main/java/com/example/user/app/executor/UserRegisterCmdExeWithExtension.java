package com.example.user.app.executor;

import com.example.user.client.dto.Response;
import com.example.user.client.dto.cmd.UserRegisterCmd;
import com.example.user.domain.extension.BizScenario;
import com.example.user.domain.extension.ExtensionExecutor;
import com.example.user.domain.extension.UserRegisterBonusExtPt;
import com.example.user.domain.extension.UserValidatorExtPt;
import com.example.user.domain.gateway.IEmailGateway;
import com.example.user.domain.gateway.ISmsGateway;
import com.example.user.domain.gateway.IUserRepository;
import com.example.user.domain.model.User;
import com.example.user.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 用户注册命令执行器（使用扩展点）
 * COLA架构：展示如何使用扩展点机制
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisterCmdExeWithExtension {

    private final IUserRepository userRepository;
    private final IEmailGateway emailGateway;
    private final ISmsGateway smsGateway;
    private final UserDomainService userDomainService;
    private final ExtensionExecutor extensionExecutor;  // 扩展点执行器

    /**
     * 执行用户注册（支持多业务场景）
     *
     * @param cmd        注册命令
     * @param bizScenario 业务场景（如：VIP用户、普通用户）
     */
    @Transactional(rollbackFor = Exception.class)
    public Response execute(UserRegisterCmd cmd, BizScenario bizScenario) {
        // 1. 构建用户领域对象
        User user = buildUser(cmd);

        // 2. 使用扩展点进行业务校验（不同场景不同规则）
        extensionExecutor.executeVoid(
                UserValidatorExtPt.class,
                bizScenario,
                ext -> ext.validate(user, bizScenario)
        );

        // 3. 校验数据唯一性
        validateUniqueness(cmd);

        // 4. 执行注册业务逻辑
        user.register(userDomainService.encryptPassword(cmd.getPassword()));

        // 5. 保存聚合根
        User savedUser = userRepository.save(user);

        // 6. 使用扩展点计算注册奖金（不同场景不同奖金）
        BigDecimal bonus = extensionExecutor.execute(
                UserRegisterBonusExtPt.class,
                bizScenario,
                ext -> ext.calculateBonus(savedUser.getId(), bizScenario)
        );

        if (bonus.compareTo(BigDecimal.ZERO) > 0) {
            log.info("用户获得注册奖金: userId={}, bonus={}", savedUser.getId(), bonus);
        }

        // 7. 发送通知
        sendNotifications(cmd, savedUser);

        log.info("用户注册成功, userId: {}, username: {}, scenario: {}",
                savedUser.getId(), savedUser.getUsername(), bizScenario);

        return Response.buildSuccess(savedUser.getId().toString());
    }

    /**
     * 构建用户领域对象
     */
    private User buildUser(UserRegisterCmd cmd) {
        User user = new User();
        user.setUsername(cmd.getUsername());
        user.setEmail(new com.example.user.domain.model.Email(cmd.getEmail()));
        user.setPhone(new com.example.user.domain.model.Phone(cmd.getPhone()));
        return user;
    }

    /**
     * 校验数据唯一性
     */
    private void validateUniqueness(UserRegisterCmd cmd) {
        if (userDomainService.isEmailExists(cmd.getEmail())) {
            throw com.example.user.client.exception.BizException.of(
                    com.example.user.client.exception.ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (userDomainService.isPhoneExists(cmd.getPhone())) {
            throw com.example.user.client.exception.BizException.of(
                    com.example.user.client.exception.ErrorCode.PHONE_ALREADY_EXISTS);
        }

        if (userDomainService.isUsernameExists(cmd.getUsername())) {
            throw com.example.user.client.exception.BizException.of(
                    com.example.user.client.exception.ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    /**
     * 发送通知
     */
    private void sendNotifications(UserRegisterCmd cmd, User user) {
        try {
            if (Boolean.TRUE.equals(cmd.getSendEmail())) {
                boolean emailSent = emailGateway.sendWelcomeEmail(
                        user.getEmail().getValue(),
                        user.getUsername()
                );
                if (!emailSent) {
                    log.warn("欢迎邮件发送失败, email: {}", user.getEmail().getValue());
                }
            }
        } catch (Exception e) {
            log.error("发送邮件异常", e);
        }

        try {
            if (Boolean.TRUE.equals(cmd.getSendSms())) {
                boolean smsSent = smsGateway.sendRegisterSms(
                        user.getPhone().getValue(),
                        user.getUsername()
                );
                if (!smsSent) {
                    log.warn("注册短信发送失败, phone: {}", user.getPhone().getValue());
                }
            }
        } catch (Exception e) {
            log.error("发送短信异常", e);
        }
    }
}
