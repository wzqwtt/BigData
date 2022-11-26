package com.wzq.chatroom.protocol;

import com.wzq.chatroom.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 必须和LengthFieldBasedFrameDecoder一起使用，不然会出现粘包半包问题
 *
 * @author wzq
 * @create 2022-11-25 16:22
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    // 魔数
    private static final String MAGIC_NUMBER = "ZQTT";
    // 版本号
    private static final Integer VERSION = 1;
    // 序列化的方式
    private static final Integer JDK_SERIALIZABLE = 0;
    private static final Integer JSON_SERIALIZABLE = 1;

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 4个字节的魔数(ZQTT)
        out.writeBytes(MAGIC_NUMBER.getBytes(StandardCharsets.UTF_8));

        // 1个字节的版本号
        out.writeByte(VERSION.byteValue());

        // 1个字节的序列化的方式
        out.writeByte(JDK_SERIALIZABLE.byteValue());

        // 1个字节的指令类型
        out.writeByte(msg.getMessageType());

        // 4个字节的请求序号
        out.writeInt(msg.getSequenceId());
        // 凑够16个字节的请求头
        out.writeByte(0xff);

        // 序列化过程，使用JDK序列化，后续修改
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);

        // 4个字节的正文长度
        byte[] bytes = bos.toByteArray();
        out.writeInt(bytes.length);

        // 写入内容
        out.writeBytes(bytes);

        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 4个字节的魔数
        int magicNum = in.readInt();

        // 1个字节的版本号
        byte version = in.readByte();

        // 1个字节的序列化方式
        byte serializerType = in.readByte();

        // 1个字节的指令类型
        byte messageType = in.readByte();

        // 4个字节的请求序列号
        int sequenceID = in.readInt();
        // 1个没有用的字节
        in.readByte();

        // 4个字节的消息正文
        int length = in.readInt();

        // 消息正文
        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();

//        log.debug("{},{},{},{},{}", magicNum, version, serializerType, messageType, sequenceID);
//        log.debug("length : {}, message : {}", length, message);

        out.add(message);
    }
}
