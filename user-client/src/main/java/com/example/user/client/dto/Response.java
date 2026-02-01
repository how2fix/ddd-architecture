package com.example.user.client.dto;

/**
 * 统一响应对象
 *
 * JDK 21 Feature: Record 类 - 不可变数据载体
 * - 自动生成构造器、getter、equals、hashCode、toString
 * - 紧凑构造函数用于自定义验证逻辑
 */
public record Response<T>(
    boolean success,
    String errCode,
    String errMessage,
    T data
) implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * JDK 21 Feature: 紧凑构造函数
     * 用于验证和规范化构造参数
     */
    public Response {
        // 成功时不应该有错误码和错误信息
        if (success && (errCode != null || errMessage != null)) {
            throw new IllegalArgumentException("Success response cannot have error code or message");
        }
        // 失败时必须有错误码
        if (!success && errCode == null) {
            throw new IllegalArgumentException("Failure response must have error code");
        }
    }

    /**
     * 成功响应（无数据）
     */
    public static Response<Void> buildSuccess() {
        return buildSuccess(null);
    }

    /**
     * 成功响应（带数据）
     */
    @SuppressWarnings("unchecked")
    public static <T> Response<T> buildSuccess(T data) {
        return new Response<>(true, null, null, data);
    }

    /**
     * 失败响应
     */
    public static <T> Response<T> buildFailure(String errCode, String errMessage) {
        return new Response<>(false, errCode, errMessage, null);
    }

    /**
     * 泛型友好的获取数据方法
     */
    public T data() {
        return data;
    }

    /**
     * 向后兼容的 isSuccess 方法
     */
    public boolean isSuccess() {
        return success;
    }
}
