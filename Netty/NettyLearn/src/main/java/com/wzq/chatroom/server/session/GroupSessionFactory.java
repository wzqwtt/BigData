package com.wzq.chatroom.server.session;

/**
 * @author wzq
 * @create 2022-11-25 15:28
 */
public class GroupSessionFactory {

    private static GroupSession session = new GroupSessionMemoryImpl();

    public static GroupSession getGroupSession() {
        return session;
    }

}
