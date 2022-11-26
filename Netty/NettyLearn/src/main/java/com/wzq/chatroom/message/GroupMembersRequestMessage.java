package com.wzq.chatroom.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author wzq
 * @create 2022-11-25 14:34
 */
@Data
@ToString(callSuper = true)
public class GroupMembersRequestMessage extends Message {
    private String groupName;

    public GroupMembersRequestMessage(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int getMessageType() {
        return GroupMembersRequestMessage;
    }
}
