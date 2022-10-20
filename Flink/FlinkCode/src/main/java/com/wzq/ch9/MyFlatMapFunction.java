package com.wzq.ch9;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

/**
 * @author wzq
 * @create 2022-10-07 21:50
 */
public class MyFlatMapFunction extends RichFlatMapFunction<Long, String> {

    // 声明状态，transient关键字的作用是：序列化对象时，被transient关键字修饰的属性不会被序列化
    private transient ValueState<Long> state;

    @Override
    public void open(Configuration parameters) throws Exception {
        // 在open生命周期方法中获取状态
        ValueStateDescriptor<Long> descriptor = new ValueStateDescriptor<>(
                "my state", // 状态名称
                Types.LONG  // 状态类型
        );
        state = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void flatMap(Long input, Collector<String> out) throws Exception {
        // 访问状态
        Long value = state.value();
        // 更新状态
        value++;
        state.update(value + 1);

        if (value >= 100) {
            out.collect("state : " + value);
            state.clear();
        }
    }
}
