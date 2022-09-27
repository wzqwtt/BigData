package com.wzq.ch6.watermark;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 *
 * 水位线生成策略
 *
 * @author wzq
 * @create 2022-09-25 22:00
 */
public class AssignTimestampsAndWatermarksDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        // 老版本设置时间语义
        // env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStreamSource<Event> stream = env.addSource(new ClickSource());

        // 设置水位线的周期
        env.getConfig().setAutoWatermarkInterval(60 * 1000L);

//        stream.assignTimestampsAndWatermarks()

        env.execute();
    }

}
