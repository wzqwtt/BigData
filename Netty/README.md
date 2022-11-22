# Netty

<img src="img/netty.png" height="150px" />

目录`NettyLearn`是一个Maven工程，里面包含使用Netty的一些案例代码

文件`Netty Figure.pptx`是一个PPT，里面画了关于Netty的图



学习路线：

| 模块          | Blog                                                         |
| ------------- | ------------------------------------------------------------ |
| 前置知识      | [NIO基础](./NIO基础.md)                                      |
|               | [Reactor模式](./Reactor模式.md)                              |
| Netty入门案例 | [第一个Netty程序](./Netty入门.md)                            |
| Netty组件     | [EventLoop和线程模型](./EventLoop和线程模型.md)              |
|               | [Channel和ChannelFuture](./Channel和ChannelFuture.md)        |
|               | [ChannelHandler & ChannelPipeline](./ChannelHandler与ChannelPipeline.md) |
|               | [Netty数据容器ByteBuf](./ByteBuf.md)                         |
|               | 编解码器Codec                                                |
| Netty应用     | 粘包与半包                                                   |
|               | 协议设计与解析                                               |
|               | 使用Netty构建一个聊天系统                                    |
| Netty优化     | 参数设置                                                     |
|               | 实现RPC                                                      |
|               | 部分源码解读                                                 |



牵扯到的几个设计模式：

- 责任链模式
- 拦截过滤器模式



# 参考资料

- [Netty官网](https://netty.io/)
- [尚硅谷Netty教程](https://www.bilibili.com/video/BV1DJ411m7NR)
- [黑马程序员Netty教程](https://www.bilibili.com/video/BV1py4y1E7oA)
- [《Netty In Action》](https://book.douban.com/subject/24700704/)