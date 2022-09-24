package com.wzq.ch5.transorm;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 物理分区 —— 轮询分区（Round-Robin）
 * <p>
 * “发牌”，按照先后顺序将数据做依次分发，平均分配
 *
 * @author wzq
 * @create 2022-09-24 20:21
 */
public class RebalanceDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<Event> source = env.addSource(new ClickSource());

        source.rebalance().print("rebalance").setParallelism(4);

        env.execute();
    }

}
