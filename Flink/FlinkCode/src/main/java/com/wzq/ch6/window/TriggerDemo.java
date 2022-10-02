package com.wzq.ch6.window;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * @author wzq
 * @create 2022-10-02 21:43
 */
public class TriggerDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
//
//        env.addSource(new ClickSource())
//                .assignTimestampsAndWatermarks(
//                        WatermarkStrategy.<Event>forMonotonousTimestamps()
//                                .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
//                                    @Override
//                                    public long extractTimestamp(Event element, long recordTimestamp) {
//                                        return element.timestamp;
//                                    }
//                                })
//                ).keyBy(e -> e.url)
//                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
////                        .trigger()

        env.execute();
    }

}
