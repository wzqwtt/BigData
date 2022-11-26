package com.wzq.chatroom.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author wzq
 * @create 2022-11-25 14:32
 */
@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message {

    private String username;
    private String password;

    public LoginRequestMessage() {
    }

    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }

}
