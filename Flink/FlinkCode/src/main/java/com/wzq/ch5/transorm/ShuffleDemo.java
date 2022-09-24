package com.wzq.ch5.transorm;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 物理分区 —— shuffle 随机分区，它服从均匀分布
 *
 * @author wzq
 * @create 2022-09-24 20:19
 */
public class ShuffleDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> source = env.addSource(new ClickSource());

        source.shuffle().print("shuffle").setParallelism(4);

        env.execute();
    }

}
