package com.wzq.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

/**
 * @author wzq
 * @create 2022-11-22 16:11
 */
public class TestByteBufComposite {

    public static void main(String[] args) {
        // 新建三个ByteBuf，并写入一些内容
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        ByteBuf buf3 = ByteBufAllocator.DEFAULT.buffer();
        buf3.writeBytes(new byte[]{11, 12, 13, 14, 15});

        // 合并为一个buf
        CompositeByteBuf buf = ByteBufAllocator.DEFAULT.compositeBuffer();
        buf.addComponents(true, buf1, buf2, buf3);
        MyByteBufUtil.log(buf);

        buf.writeBytes(new byte[]{99, 98, 100, 101});
        MyByteBufUtil.log(buf);

        // 因为composite也是零拷贝，所以修改buf1，buf也会改变
        buf1.setByte(0, 50);
        MyByteBufUtil.log(buf);
    }

}
