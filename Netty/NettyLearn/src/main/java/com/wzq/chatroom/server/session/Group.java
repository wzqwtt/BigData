package com.wzq.chatroom.server.session;

import lombok.Data;

import java.util.Collections;
import java.util.Set;

/**
 * 聊天组
 *
 * @author wzq
 * @create 2022-11-25 15:02
 */
@Data
public class Group {

    // 聊天室名称
    private String name;

    // 聊天室成员
    private Set<String> members;

    public static final Group EMPTY_GROUP = new Group("empty", Collections.emptySet());

    public Group(String name, Set<String> members) {
        this.name = name;
        this.members = members;
    }
}
