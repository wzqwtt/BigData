package com.wzq.ch6.watermark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;

/**
 * @author wzq
 * @create 2022-09-27 20:53
 */
public class WatermarkAPIDemo1 {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 1,e01,121312367,pg01
        DataStreamSource<String> stream = env.socketTextStream("localhost", 9999);

        // 从map阶段生成watermark，map下游的算法也都具有水位线
        SingleOutputStreamOperator<EventBean> streamMap = stream.map(s -> {
                    String[] split = s.split(",");
                    return new EventBean(Long.parseLong(split[0]), split[1], Long.parseLong(split[2]), split[3]);
                }).returns(EventBean.class)
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<EventBean>forBoundedOutOfOrderness(Duration.ofMillis(2000))
                                .withTimestampAssigner(new SerializableTimestampAssigner<EventBean>() {
                                    @Override
                                    public long extractTimestamp(EventBean element, long recordTimestamp) {
                                        return element.getTimestamp();
                                    }
                                })
                );

        // 观察watermark
        streamMap.process(new ProcessFunction<EventBean, EventBean>() {
            @Override
            public void processElement(EventBean value, ProcessFunction<EventBean, EventBean>.Context ctx, Collector<EventBean> out) throws Exception {
                // 打印watermark
                long watermark = ctx.timerService().currentWatermark();

                // 处理时间
                long processTime = ctx.timerService().currentProcessingTime();

                System.out.println("本次收到的数据：" + value);
                System.out.println("此刻的watermark：" + watermark);
                System.out.println("此刻的处理时间：" + processTime);

                out.collect(value);
            }
        }).print();

        env.execute();
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class EventBean {
    private long guid;
    private String eventId;
    private long timestamp;
    private String pageId;

}
