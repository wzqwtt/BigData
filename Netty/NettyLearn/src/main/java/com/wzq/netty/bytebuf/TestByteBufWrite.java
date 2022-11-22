package com.wzq.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;


/**
 * 测试写入ByteBuf的方法
 *
 * @author wzq
 * @create 2022-11-21 15:46
 */
public class TestByteBufWrite {

    public static void main(String[] args) {
        // 创建ByteBuf，初始容量是16，最大容量是20
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(16, 20);
        MyByteBufUtil.log(buf);

        // 向ByteBuf中写入数据
        // 写入一组byte[]数组
        buf.writeBytes(new byte[]{'a', 98, 99, 100});    // 4个字节
        MyByteBufUtil.log(buf);

        // 大端写入一个int值
        buf.writeInt(1);    // 4个字节
        MyByteBufUtil.log(buf);

        // 小端写入一个Int值
        buf.writeIntLE(2);    // 4个字节
        MyByteBufUtil.log(buf);

        // 写入一个Long值
        buf.writeLong(3);   // 8个字节
        MyByteBufUtil.log(buf);

        // 已经到达最大容量了，即使再添加一个字节也会报错
        buf.writeByte(1);
    }

}
