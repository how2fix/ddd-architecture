package com.example.user.client.dto.cmd;

import com.example.user.client.dto.Command;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户注册命令
 * CQRS: Command对象用于写操作
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserRegisterCmd extends Command {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    private String username;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 是否发送邮件通知
     */
    private Boolean sendEmail = true;

    /**
     * 是否发送短信通知
     */
    private Boolean sendSms = true;

    // ==================== 扩展字段（不同注册方式使用） ====================

    /**
     * 验证码（邮箱验证码或短信验证码）
     */
    private String verifyCode;

    /**
     * 注册方式：email, phone, wechat
     */
    private String registerType;

    /**
     * 微信OpenID
     */
    private String wechatOpenId;

    /**
     * 微信UnionID
     */
    private String wechatUnionId;

    /**
     * 微信昵称
     */
    private String wechatNickname;

    /**
     * 微信头像URL
     */
    private String wechatAvatar;

    // ==================== 企业用户字段 ====================

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 营业执照号/图片URL
     */
    private String businessLicense;

    /**
     * 统一社会信用代码
     */
    private String creditCode;

    /**
     * 企业联系人姓名
     */
    private String contactName;

    /**
     * 企业联系电话
     */
    private String contactPhone;
}
