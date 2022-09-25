package com.wzq.ch5.sink;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

/**
 * Kafka Sink
 *
 * @author wzq
 * @create 2022-09-25 19:48
 */
public class ToKafka {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092,hadoop103:9092");

        DataStreamSource<Event> stream = env.addSource(new ClickSource());

        stream.map(data -> data.toString())
                .addSink(new FlinkKafkaProducer<String>(
                        "clicks",
                        new SimpleStringSchema(),
                        properties
                ));

        env.execute();
    }

}
