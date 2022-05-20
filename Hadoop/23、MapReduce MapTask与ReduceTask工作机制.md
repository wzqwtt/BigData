[toc]


------


# 一、MapTask
## 1、MapTask工作机制
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420154927351.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
- `Read`阶段：`MapTask`通过`InputFormat`获得的`RecordReader`，从输入`InputSplit`中解析出一个个的`k-v`
- `Map`阶段：该节点主要是将解析出的`k-v`交给用户编写的`map()`方法处理，并产生一系列新的`k-v`
- `Collect`收集阶段：在用户编写`map()`方法中，当数据处理完成后一般会调用`OutputCollector.collect()`输出结果。在该方法内部，它会将新生成的`k-v`分区（即调用`Partitioner`），并写入一个环形内存缓冲区中
- `Spill`阶段：即==溢写==，当环形缓冲区满后，`MapReduce`会将数据写到本地磁盘上，生产一个临时文件。在数据写入磁盘之前，先要对数据进行一次本地排序，并在必要时对数据进行合并、压缩等操作
	>溢写阶段详情：
	> - 步骤1：采用快速排序算法对缓存区内的数据进行排序，排序方式是，先按照分区编号`Partition`进行排序，然后按照`key`进行排序。这样，经过排序后，数据以分区为单位聚集在一起，且同一分区内所有数据都按照`key`有序
	> - 步骤2：按照分区编号由小到大依次将每个分区中的数据写入任务工作目录下的临时文件`output/spillN.out`（N表示当前溢写次数）中，如果用户设置了`Combiner`，则写入文件之前，对每个分区中的数据进行一次聚集操作
	> - 步骤3：将分区数据的元信息写到内存索引数据结构`SpillRecord`中，其中每个分区的元信息包括在临时文件中的偏移量、压缩前数据大小和压缩后数据大小。如果当前内存索引大小超过`1MB`，则将内存索引写到文件`output/spillN.out.index`中
- `Merge`阶段：当所有数据处理完成后，`MapTask`对所有临时文件进行一次合并，以确保最终只会生产一个数据文件


## 2、MapTask源码解析

```java
context.write(k, NullWritable.get()); //自定义的 map 方法的写出，进入
	output.write(key, value); 
		//MapTask727 行，收集方法，进入两次
		collector.collect(key, value,partitioner.getPartition(key, value, partitions));
			HashPartitioner(); //默认分区器
		collect() //MapTask1082 行 map 端所有的 kv 全部写出后会走下面的 close 方法
			close() //MapTask732 行
				collector.flush() // 溢出刷写方法，MapTask735 行，提前打个断点，进入
					sortAndSpill() //溢写排序，MapTask1505 行，进入
						sorter.sort() QuickSort //溢写排序方法，MapTask1625 行，进入
					mergeParts(); //合并文件，MapTask1527 行，进入
				collector.close(); //MapTask739 行,收集器关闭,即将进入 ReduceTask
```

# 二、ReduceTask
## 1、ReduceTask工作机制
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420160111985.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
- `Copy`阶段：`ReduceTask`从各个`MapTask`上远程拷贝一片数据，并针对某一片数据，如果其大小超过一定的阙值，则写到磁盘上，否则直接放到内存中
- `Sort`阶段：在远程拷贝数据的同时，`ReduceTasl`启动了两个后台线程对内存和磁盘上的文件进行合并，以防止内存使用过多或磁盘上的文件过多，按照`MapReduce`语义，用户编写`reduce()`函数输入数据是按`key`进行聚集的一组数据。为了将`key`相同的数据聚在一起，`Hadoop`采用了基于排序的策略，由于各个`MapTask`已经实现对自己的处理结果并进行了局部排序，因此`RedecuTask`只需对所有数据进行一次归并排序即可
- `Reduce`阶段：`reduce()`函数将计算结果写到`HDFS`上
## 2、ReduceTask并行度决定机制
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021042016052262.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420160531499.png)
注意事项：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210420160542229.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xlc2lsZXFpbg==,size_16,color_FFFFFF,t_70)

## 3、ReduceTask源码解析
```java
if (isMapOrReduce()) //reduceTask324 行，提前打断点
	initialize() // reduceTask333 行,进入
	init(shuffleContext); // reduceTask375 行,走到这需要先给下面的打断点
		 totalMaps = job.getNumMapTasks(); // ShuffleSchedulerImpl 第 120 行，提前打断点
		 merger = createMergeManager(context); //合并方法，Shuffle 第 80 行
			// MergeManagerImpl 第 232 235 行，提前打断点
			this.inMemoryMerger = createInMemoryMerger(); //内存合并
			this.onDiskMerger = new OnDiskMerger(this); //磁盘合并
	rIter = shuffleConsumerPlugin.run();
		eventFetcher.start(); //开始抓取数据，Shuffle 第 107 行，提前打断点
		eventFetcher.shutDown(); //抓取结束，Shuffle 第 141 行，提前打断点
		copyPhase.complete(); //copy 阶段完成，Shuffle 第 151 行
		taskStatus.setPhase(TaskStatus.Phase.SORT); //开始排序阶段，Shuffle 第 152 行
	sortPhase.complete(); //排序阶段完成，即将进入 reduce 阶段 reduceTask382 行
reduce(); //reduce 阶段调用的就是我们自定义的 reduce 方法，会被调用多次
	cleanup(context); //reduce 完成之前，会最后调用一次 Reducer 里面的 cleanup 方法
```
