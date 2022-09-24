package com.wzq.ch5.transorm;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 物理分区 —— Broadcast 广播
 * <p>
 * 将数据复制并发送到下游算子的所有并行子任务中去
 *
 * @author wzq
 * @create 2022-09-24 20:30
 */
public class BroadcastDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> stream = env.addSource(new ClickSource());

        stream.broadcast().print("broadcast").setParallelism(4);

        env.execute();
    }

}
