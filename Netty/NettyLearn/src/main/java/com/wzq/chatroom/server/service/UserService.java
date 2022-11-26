package com.wzq.chatroom.server.service;

/**
 * 用户管理接口
 *
 * @author wzq
 * @create 2022-11-25 15:29
 */
public interface UserService {

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回true，否则返回false
     */
    boolean login(String username, String password);

}
