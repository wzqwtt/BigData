package com.wzq.chatroom.client.handler;

import com.wzq.chatroom.message.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wzq
 * @create 2022-11-25 19:39
 */
@Slf4j
public class MonitorHandler extends ChannelInboundHandlerAdapter {

    // 倒计时锁，用于主次线程之间的通信
    // 初始计数为1，减为0才继续往下运行，否则等待
    CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
    // 登录状态，初始值false 【主次线程之间的共享变量】
    AtomicBoolean LOGIN = new AtomicBoolean(false);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        new Thread(() -> {
            // 用户输入
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入用户名:");
            String username = scanner.nextLine();
            System.out.println("请输入密码:");
            String password = scanner.nextLine();

            // 创建包含登录信息的请求体
            LoginRequestMessage message = new LoginRequestMessage(username, password);
            // 发送到Channel中
            ctx.writeAndFlush(message);

            System.out.println("等待后续操作...");

            // 阻塞，直到登录成功后CountDownLatch被设置为0
            try {
                WAIT_FOR_LOGIN.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 如果登录失败，退出程序！
            if (!LOGIN.get()) {
                ctx.channel().close();
                return;
            }

            System.out.println(username + "登录成功，使用以下命令开始聊天：");
            printCommand();


            while (!Thread.interrupted()) {
                System.out.print("> ");
                String command = scanner.nextLine();

                // TODO 校验命令格式

                // 解析命令
                String[] commands = command.split(" ");

                switch (commands[0]) {
                    case "send":
                        // 单聊，发送信息
                        // TODO 防止空格！拼接commands[2]之后的消息
                        ctx.writeAndFlush(new ChatRequestMessage(username, commands[1], commands[2]));
                        break;
                    case "gsend":
                        // 往群聊里面发信息
                        ctx.writeAndFlush(new GroupChatRequestMessage(username, commands[1], commands[2]));
                        break;
                    case "gcreate":
                        // 创建群聊
                        System.out.println("创建群聊: " + commands[1] + ", 群聊成员: " + commands[2]);
                        Set<String> set = new HashSet<>(Arrays.asList(commands[2].split(",")));
                        set.add(username);
                        ctx.writeAndFlush(new GroupCreateRequestMessage(commands[1], set));
                        break;
                    case "gmembers":
                        // 获取某个群聊的所有成员
                        ctx.writeAndFlush(new GroupMembersRequestMessage(commands[1]));
                        break;
                    case "gjoin":
                        // 加入群聊
                        ctx.writeAndFlush(new GroupJoinRequestMessage(username, commands[1]));
                        break;
                    case "gquit":
                        // 退出群聊
                        ctx.writeAndFlush(new GroupQuitRequestMessage(username, commands[1]));
                        break;
                    case "quit":
                        // TODO 解决退出问题！
                        ctx.channel().close();
                        Thread.currentThread().interrupt();
                        break;
                    case "help":
                        printCommand();
                        break;
                    default:
                        System.out.println("命令错误！请重试！");
                }
            }
        }, "login channel").start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.debug("{}", msg);
        if (msg instanceof LoginResponseMessage) {
            // 如果是登录的响应消息
            LoginResponseMessage message = (LoginResponseMessage) msg;

            boolean isSuccess = message.isSuccess();

            // 如果登录成功
            if (isSuccess) {
                LOGIN.set(true);
            }

            // 登陆后，唤醒登录线程
            WAIT_FOR_LOGIN.countDown();
        } else if (msg instanceof AbstractResponseMessage) {
            printResponse((AbstractResponseMessage) msg);
        }
    }

    public void printResponse(AbstractResponseMessage message) {
        if (message.isSuccess()) {
            // 信息发送成功
            System.out.println(message.getReason());
        } else {
            System.out.println(message.getReason());
        }
        System.out.print("> ");
    }

    public void printCommand() {
        System.out.println("==================================");
        System.out.println("send [username] [content]");
        System.out.println("gsend [group name] [content]");
        System.out.println("gcreate [group name] [m1,m2,m3...]");
        System.out.println("gmembers [group name]");
        System.out.println("gjoin [group name]");
        System.out.println("gquit [group name]");
        System.out.println("quit");
        System.out.println("help");
        System.out.println("==================================");
    }

}
