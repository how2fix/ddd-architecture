package com.example.user.client.dto;

import java.io.Serializable;

/**
 * Command基类
 * CQRS: Command用于写操作
 */
public abstract class Command implements Serializable {

    private static final long serialVersionUID = 1L;
}
