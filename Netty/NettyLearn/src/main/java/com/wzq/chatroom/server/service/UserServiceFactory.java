package com.wzq.chatroom.server.service;

/**
 * @author wzq
 * @create 2022-11-25 15:32
 */
public class UserServiceFactory {

    private static UserService userService = new UserServiceMemoryImpl();

    public static UserService getUserService() {
        return userService;
    }

}
