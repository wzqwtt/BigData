package com.wzq.ch5.sink;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 传输数据到MySQL（JDBC）
 *
 * @author wzq
 * @create 2022-09-25 20:12
 */
public class ToMySQL {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);

        DataStreamSource<Event> stream = env.addSource(new ClickSource());

        stream.addSink(
                JdbcSink.sink(
                        "insert into clicks (user,url) values (?,?)",
                        (statement, r) -> {
                            statement.setString(1, r.user);
                            statement.setString(2, r.url);
                        },
                        JdbcExecutionOptions.builder()
                                .withBatchSize(1000)
                                .withBatchIntervalMs(200)
                                .withMaxRetries(5)
                                .build(),
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl("jdbc:mysql://hadoop102:3306/FlinkDemo?useSSL=false")
                                .withDriverName("com.mysql.cj.jdbc.Driver")
                                .withUsername("root")
                                .withPassword("root")
                                .build()
                )
        );

        env.execute();

    }

}
