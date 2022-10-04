package com.wzq.ch8;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 使用filter对DataStream进行简单分流
 *
 * @author wzq
 * @create 2022-10-04 15:14
 */
public class SplitStreamByFilter {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);

        DataStreamSource<Event> allDataStream = env.addSource(new ClickSource());

        // 分流wzq
        allDataStream.filter(e -> e.user.equals("wzq")).print("wzq");

        // 分流wtt
        allDataStream.filter(e -> e.user.equals("wtt")).print("wtt");

        // 其他
        allDataStream.filter(e -> !e.user.equals("wzq") && !e.user.equals("wtt")).print("else");

        env.execute();
    }
}
