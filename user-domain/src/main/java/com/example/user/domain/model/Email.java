package com.example.user.domain.model;

import lombok.Data;

import java.util.regex.Pattern;

/**
 * 邮箱值对象
 * 值对象特征：不可变、有验证逻辑
 */
@Data
public class Email {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private String value;

    public Email(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("邮箱格式不正确: " + value);
        }
        this.value = value;
    }

    public static boolean isValid(String value) {
        return value != null && EMAIL_PATTERN.matcher(value).matches();
    }

    @Override
    public String toString() {
        return value;
    }
}
