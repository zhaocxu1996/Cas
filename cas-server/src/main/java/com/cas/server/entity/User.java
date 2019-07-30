package com.cas.server.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 账户表
 * </p>
 *
 * @author zhaocxu
 */
@Data
public class User {

    /**
     * 主键
     */
    private Long id;

    /**
     * 账号
     */
    private String name;

    /**
     * 密码
     */
    private String password;

}
