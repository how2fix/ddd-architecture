package com.example.user.domain.model;

import lombok.Data;

import java.util.regex.Pattern;

/**
 * 手机号值对象
 */
@Data
public class Phone {

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^1[3-9]\\d{9}$");

    private String value;

    public Phone(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("手机号格式不正确: " + value);
        }
        this.value = value;
    }

    public static boolean isValid(String value) {
        return value != null && PHONE_PATTERN.matcher(value).matches();
    }

    @Override
    public String toString() {
        return value;
    }
}
