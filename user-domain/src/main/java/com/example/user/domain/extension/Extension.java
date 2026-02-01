package com.example.user.domain.extension;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 扩展点注解
 * COLA架构：标记扩展点实现
 *
 * 使用示例：
 * <pre>
 * &#64;Extension(bizId = "VIP")
 * public class VipUserValidator implements UserValidatorExtPt {
 *     ...
 * }
 *
 * &#64;Extension(bizId = "VIP", useCase = "register", scenario = "email")
 * public class VipEmailRegisterValidator implements UserValidatorExtPt {
 *     ...
 * }
 * </pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Extension {

    /**
     * 业务身份
     * 如：VIP, NORMAL, TMALL
     */
    String bizId() default "DEFAULT";

    /**
     * 用例
     * 如：register, login, order
     */
    String useCase() default "";

    /**
     * 场景
     * 如：email, phone, wechat
     */
    String scenario() default "";

    /**
     * 扩展点描述
     */
    String description() default "";
}
