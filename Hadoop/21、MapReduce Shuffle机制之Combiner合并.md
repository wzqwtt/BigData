# Combiner合并机制

- `Combiner`合并也属于`Shuffle`机制
- `Combiner`的父类是`Reducer`
- `Combiner`和`Reducer`的区别在于运行时的位置
	- `Combiner`是在每一个`MapTask`所在的节点运行的
	- `Reducer`接收全局所有`Mapper`的输出结果
- `Combiner`的意义就是对每一个`MapTask`的输出进行局部汇总，**主要目的是为了减小网络的传输量**
- 并不是所有的场景都能适用`Combiner`，一般主要用于求和操作

实现`Combiner`的步骤就是继承`Reducer`，最后在`Driver`类通过`setCombinerClass(类.class)`设置进去就好了



