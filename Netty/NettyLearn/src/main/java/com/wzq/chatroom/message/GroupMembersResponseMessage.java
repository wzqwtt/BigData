package com.wzq.chatroom.message;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

/**
 * @author wzq
 * @create 2022-11-25 14:46
 */
@Data
@ToString(callSuper = true)
public class GroupMembersResponseMessage extends AbstractResponseMessage {

    private Set<String> members;

    public GroupMembersResponseMessage(Set<String> members) {
        this.members = members;
    }

    public GroupMembersResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GroupMembersResponseMessage;
    }
}
