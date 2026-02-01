package com.example.user.infrastructure.constant;

/**
 * 用户相关 SQL 常量
 *
 * JDK 21 Feature: Text Blocks (文本块)
 * - 使用 """ 包裹多行字符串
 * - 自动格式化，无需拼接字符串
 * - 适合 SQL、JSON、HTML 等多行文本
 */
public final class UserSqlConstants {

    /**
     * 创建用户表 SQL
     * JDK 21 Feature: Text Blocks - 更清晰的多行 SQL
     */
    public static final String CREATE_USER_TABLE = """
            CREATE TABLE IF NOT EXISTS t_user (
                id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                username   VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
                email      VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
                phone      VARCHAR(20)  NOT NULL UNIQUE COMMENT '手机号',
                password   VARCHAR(100) NOT NULL COMMENT '密码',
                status     VARCHAR(20)  NOT NULL DEFAULT 'INACTIVE' COMMENT '用户状态',
                register_time TIMESTAMP   COMMENT '注册时间',
                last_active_time TIMESTAMP COMMENT '最后活跃时间',
                create_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                update_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
            )
            """;

    /**
     * 查询用户详情 SQL（带关联）
     */
    public static final String SELECT_USER_WITH_DETAILS = """
            SELECT u.id,
                   u.username,
                   u.email,
                   u.phone,
                   u.status,
                   u.register_time,
                   u.last_active_time
            FROM t_user u
            WHERE u.id = :userId
            """;

    /**
     * 统计用户 SQL
     */
    public static final String COUNT_USERS_BY_STATUS = """
            SELECT status,
                   COUNT(*) as count
            FROM t_user
            WHERE status IN (:statuses)
            GROUP BY status
            """;

    /**
     * JSON 示例：用户配置
     * JDK 21 Feature: Text Blocks for JSON
     */
    public static final String USER_CONFIG_EXAMPLE = """
            {
                "version": "1.0",
                "features": {
                    "email_notification": true,
                    "sms_notification": true,
                    "max_login_attempts": 5
                },
                "security": {
                    "password_min_length": 6,
                    "password_require_special_char": false
                }
            }
            """;

    private UserSqlConstants() {
        // 工具类，禁止实例化
    }
}
