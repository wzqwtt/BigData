package com.wzq.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wzq
 * @create 2022-11-22 14:10
 */
@Slf4j
public class TestByteBufRead {

    public static void main(String[] args) {
        // 创建ByteBuf并写入一些字节
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(new byte[]{97, 98, 99, 100, 101, 102, 103});

        // 先读取2个数据
        System.out.println((char) buf.readByte());
        System.out.println((char) buf.readByte());

        // 打印查看，此时read index在2
        MyByteBufUtil.log(buf);

        // 标记2为读取的位置
        buf.markReaderIndex();
        // 继续读两个数据
        System.out.println((char) buf.readByte());
        System.out.println((char) buf.readByte());

        // 此时read index在4
        MyByteBufUtil.log(buf);

        // 重复读取，重置readindex标记到2
        buf.resetReaderIndex();
        MyByteBufUtil.log(buf);
    }

}
