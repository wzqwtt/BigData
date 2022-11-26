package com.wzq.chatroom.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author wzq
 * @create 2022-11-25 14:38
 */
@Data
@ToString(callSuper = true)
public class LoginResponseMessage extends AbstractResponseMessage {

    public LoginResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }
}
