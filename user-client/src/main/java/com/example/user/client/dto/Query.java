package com.example.user.client.dto;

import java.io.Serializable;

/**
 * Query基类
 * CQRS: Query用于读操作
 */
public abstract class Query implements Serializable {

    private static final long serialVersionUID = 1L;
}
