package com.wzq.chatroom.server.session;

/**
 * @author wzq
 * @create 2022-11-25 14:55
 */
public class SessionFactory {

    private static Session session = new SessionMemoryImpl();

    public static Session getSession() {
        return session;
    }

}
