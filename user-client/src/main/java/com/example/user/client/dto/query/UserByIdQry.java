package com.example.user.client.dto.query;

import com.example.user.client.dto.Query;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 根据ID查询用户
 * CQRS: Query对象用于读操作
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserByIdQry extends Query {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
