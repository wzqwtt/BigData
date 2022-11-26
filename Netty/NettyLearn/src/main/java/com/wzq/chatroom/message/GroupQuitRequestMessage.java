package com.wzq.chatroom.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author wzq
 * @create 2022-11-25 14:35
 */
@Data
@ToString(callSuper = true)
public class GroupQuitRequestMessage extends Message {
    private String groupName;

    private String username;

    public GroupQuitRequestMessage(String username, String groupName) {
        this.groupName = groupName;
        this.username = username;
    }

    @Override
    public int getMessageType() {
        return GroupQuitRequestMessage;
    }
}