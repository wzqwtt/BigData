package com.wzq.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * 测试使用
 *
 * @author wzq
 * @create 2022-11-22 14:54
 */
public class TestByteBufSlice {
    public static void main(String[] args) {
        // 创建一个ByteBuf，并为其写入一些数据
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        MyByteBufUtil.log(buf);

        // 在ByteBuf切片的过程中，数据没有发生复制
        // slice方法传入两个参数，第一个参数是索引开始的位置，第二个参数是切片的长度
        ByteBuf bufSlice1 = buf.slice(0, 5);
        ByteBuf bufSlice2 = buf.slice(5, 5);

        System.out.println("bufSlice1:");
        MyByteBufUtil.log(bufSlice1);
        System.out.println("bufSlice2:");
        MyByteBufUtil.log(bufSlice2);

        // 两个切片的引用计数+1
        bufSlice1.retain();
        bufSlice2.retain();

        // 我们可以在bufSlice1上修改一个值，原有的大buf对应的索引也会改变
        bufSlice1.setByte(0, 'z');
        // 打印查看
        System.out.println("buf索引为0的位置发生改变:");
        MyByteBufUtil.log(buf);

        // 如果我们直接释放了buf并且slice没有+1，那么切片后的两个slice也不能继续正常使用了
        // 现在对两个slice的引用计数执行了+1操作，因此即使大buf释放，小buf也可以正常使用
        buf.release();

        // 再次尝试修改bufSlice2的数据，将会报错
        bufSlice2.setByte(0, 'y');
    }
}
