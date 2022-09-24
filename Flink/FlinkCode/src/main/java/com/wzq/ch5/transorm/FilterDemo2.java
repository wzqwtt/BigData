package com.wzq.ch5.transorm;

import com.wzq.ch5.source.ClickSource;
import com.wzq.ch5.source.Event;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Filter过滤
 *
 * @author wzq
 * @create 2022-09-23 21:31
 */
public class FilterDemo2 {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);


        // 匿名内部类
        env.addSource(new ClickSource()).filter(new FilterFunction<Event>() {
            @Override
            public boolean filter(Event value) throws Exception {
                return value.url.contains("home");
            }
        }).print("匿名内部类");

        // lambda表达式
        env.addSource(new ClickSource()).filter(e -> e.url.contains("home")).print("lambda");

        // FilterFunction实现类
        env.addSource(new ClickSource()).filter(new MyFilter("home")).print("实现类");

        env.execute();
    }

    public static class MyFilter implements FilterFunction<Event> {

        private String keyword;

        public MyFilter(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public boolean filter(Event value) throws Exception {
            return value.url.contains(keyword);
        }
    }

}
