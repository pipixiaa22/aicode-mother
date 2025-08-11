package com.ckrey.ckreycodemother.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserVO implements Serializable {

    private Long id;

    /**
     * 账号
     */
    private String useraccount;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户头像
     */
    private String useravatar;

    /**
     * 用户简介
     */
    private String userprofile;

    /**
     * 用户角色：user/admin
     */
    private String userrole;

    /**
     * 创建时间
     */
    private LocalDateTime createtime;

    private static final long serialVersionUID = 1L;
}
