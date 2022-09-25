package com.wzq.ch5.sink;

import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.util.concurrent.TimeUnit;

/**
 * 输出到文件
 *
 * @author wzq
 * @create 2022-09-24 20:51
 */
public class ToFile {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);

        DataStreamSource<Event> stream = env.fromElements(
                new Event("wzq1", "./home", 1000L),
                new Event("wzq2", "./home", 2000L),
                new Event("wzq3", "./home", 3000L),
                new Event("wzq4", "./home", 4000L),
                new Event("wzq5", "./home", 5000L),
                new Event("wzq6", "./home", 6000L),
                new Event("wzq7", "./home", 7000L),
                new Event("wzq8", "./home", 8000L),
                new Event("wzq9", "./home", 9000L),
                new Event("wzq10", "./home", 10000L)
        );

        StreamingFileSink<String> streamFileSink = StreamingFileSink.<String>forRowFormat(
                        new Path("./input/output"),
                        new SimpleStringEncoder<>("UTF-8")
                ).withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withMaxPartSize(1024 * 1024 * 1024)
                                .withRolloverInterval(TimeUnit.MINUTES.toMillis(15))
                                .withInactivityInterval(TimeUnit.MINUTES.toMillis(5))
                                .build()
                )
                .build();

        stream.map(data -> data.toString())
                .addSink(streamFileSink);

        env.execute();
    }

}
