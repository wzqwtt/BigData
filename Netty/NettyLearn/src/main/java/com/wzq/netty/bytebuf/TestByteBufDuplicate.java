package com.wzq.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * ByteBuf Duplicate
 *
 * @author wzq
 * @create 2022-11-22 16:06
 */
public class TestByteBufDuplicate {

    public static void main(String[] args) {
        // 新建ByteBuf，然后填充一些内容
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'h', 'i', 'j', 'k'});

        // 使用duplicate进行零拷贝
        ByteBuf dupBuf = buf.duplicate();
        dupBuf.retain();    // 引用计数+1
        MyByteBufUtil.log(dupBuf);

        // 对dupBuf进行修改，buf也会改变
        dupBuf.setByte(0, 'z');
        MyByteBufUtil.log(buf);

        // 释放内存
        buf.release();
        dupBuf.release();
    }

}
