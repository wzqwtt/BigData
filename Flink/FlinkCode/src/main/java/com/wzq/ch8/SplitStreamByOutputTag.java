package com.wzq.ch8;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * 使用处理函数中的Side Output进行分流
 *
 * @author wzq
 * @create 2022-10-04 15:19
 */
public class SplitStreamByOutputTag {

    // 定义输出标签
    private static OutputTag<Tuple3<String, String, Long>> wzqTag = new OutputTag<Tuple3<String, String, Long>>("wzq") {};
    private static OutputTag<Tuple3<String, String, Long>> wttTag = new OutputTag<Tuple3<String, String, Long>>("wtt") {};

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        SingleOutputStreamOperator<Event> processedStream = env.addSource(new ClickSource())
                .process(new ProcessFunction<Event, Event>() {
                    @Override
                    public void processElement(Event value, ProcessFunction<Event, Event>.Context ctx, Collector<Event> out) throws Exception {
                        // 进行分流
                        if (value.user.equals("wzq")) {
                            // Context对象中的output方法即为输出到侧输出流的方法
                            ctx.output(wzqTag, Tuple3.of(value.user, value.url, value.timestamp));
                        } else if (value.user.equals("wtt")) {
                            ctx.output(wttTag, Tuple3.of(value.user, value.url, value.timestamp));
                        } else {
                            out.collect(value);
                        }
                    }
                });

        // 输出
        processedStream.getSideOutput(wzqTag).print("wzq Tag");
        processedStream.getSideOutput(wttTag).print("wtt Tag");
        processedStream.print("else Tag");

        env.execute();
    }
}
