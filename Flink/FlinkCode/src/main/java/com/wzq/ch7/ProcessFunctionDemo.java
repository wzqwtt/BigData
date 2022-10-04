package com.wzq.ch7;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

/**
 * ProcessFunction处理函数简单应用
 *
 * @author wzq
 * @create 2022-10-02 22:33
 */
public class ProcessFunctionDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.addSource(new ClickSource())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Event>forMonotonousTimestamps()
                                .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                                    @Override
                                    public long extractTimestamp(Event element, long recordTimestamp) {
                                        return element.timestamp;
                                    }
                                })
                ).process(new ProcessFunction<Event, String>() {
                    // 处理函数需要传入ProcessFunction抽象类的实现类，需要实现processElement方法
                    @Override
                    public void processElement(Event value, ProcessFunction<Event, String>.Context ctx, Collector<String> out) throws Exception {
                        if (value.user.equals("wtt")) {
                            out.collect(value.user);
                        } else if (value.user.equals("wzq")) {
                            out.collect(value.user);
                            out.collect(value.url);
                        }
                        System.out.println(ctx.timerService().currentWatermark());
                    }
                }).print();

        env.execute();
    }
}