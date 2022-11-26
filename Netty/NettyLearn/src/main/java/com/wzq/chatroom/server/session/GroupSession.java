package com.wzq.chatroom.server.session;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Set;

/**
 * 聊天组会话接口
 *
 * @author wzq
 * @create 2022-11-25 15:03
 */
public interface GroupSession {

    /**
     * 判断组是否存在
     *
     * @param name 组名
     * @return 如果存在返回true，否则返回false
     */
    boolean IsCreated(String name);

    /**
     * 创建一个聊天组，如果不存在才能创建成功
     *
     * @param name    组名
     * @param members 成员
     * @return 成功时返回null，失败返回原来的value
     */
    Group createGroup(String name, Set<String> members);

    /**
     * 加入聊天组
     *
     * @param name   组名
     * @param member 成员名
     * @return 如果组不存在返回null，否则返回组对象
     */
    Group joinMember(String name, String member);

    /**
     * 移除组成员
     *
     * @param name   组名
     * @param member 成员名
     * @return 如果组不存在返回null，否则返回组对象
     */
    Group removeMember(String name, String member);

    /**
     * 移除聊天组
     *
     * @param name 组名
     * @return 如果组不存在返回null，否则返回组对象
     */
    Group removeGroup(String name);

    /**
     * 获取组成员
     *
     * @param name 组名
     * @return 成员集合，没有成员返回 empty set
     */
    Set<String> getMembers(String name);

    /**
     * 获取组成员的Channel集合，只有在线的Channel才会返回
     *
     * @param name 组名
     * @return 成员channel集合
     */
    List<Channel> getMembersChannel(String name);


}
