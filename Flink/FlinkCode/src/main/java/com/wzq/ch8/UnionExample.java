package com.wzq.ch8;

import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;

/**
 * 联合（Union）例子
 *
 * @author wzq
 * @create 2022-10-04 15:31
 */
public class UnionExample {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<Event> hadoop102Stream = socketStream(env, "hadoop102", 7777, Duration.ofSeconds(2));
        hadoop102Stream.print("hadoop102");

        DataStream<Event> hadoop103Stream = socketStream(env, "hadoop103", 7777, Duration.ofSeconds(5));
        hadoop103Stream.print("hadoop103");

        // 合并两条流，要求两条流的数据类型一致
        hadoop102Stream.union(hadoop103Stream)
                .process(new ProcessFunction<Event, String>() {
                    @Override
                    public void processElement(Event value, ProcessFunction<Event, String>.Context ctx, Collector<String> out) throws Exception {
                        out.collect("水位线：" + ctx.timerService().currentWatermark());
                    }
                }).print();

        env.execute();
    }

    // 获取流
    public static DataStream<Event> socketStream(StreamExecutionEnvironment env, String hostname, int port, Duration time) {
        SingleOutputStreamOperator<Event> stream = env.socketTextStream(hostname, port)
                .map(data -> {
                    String[] split = data.split(",");
                    return new Event(split[0].trim(), split[1].trim(), Long.parseLong(split[2].trim()));
                })
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Event>forBoundedOutOfOrderness(time)
                                .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                                    @Override
                                    public long extractTimestamp(Event element, long recordTimestamp) {
                                        return element.timestamp;
                                    }
                                })
                );
        return stream;
    }

}
