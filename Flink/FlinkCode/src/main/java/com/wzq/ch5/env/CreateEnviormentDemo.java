package com.wzq.ch5.env;

import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 三种创建Flink执行环境的方式
 *
 * @author wzq
 * @create 2022-09-18 20:02
 */
public class CreateEnviormentDemo {

    public static void main(String[] args) {
        // 1、根据当前程序运行的上下文，自动创建Flink的执行环境
        StreamExecutionEnvironment env1 = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2、创建本地环境，可以传递一个参数parallism设置并行度，默认情况下，并行度等于本地CPU核心数量
        StreamExecutionEnvironment env2 = StreamExecutionEnvironment.createLocalEnvironment(2);
        // StreamExecutionEnvironment env3 = StreamExecutionEnvironment.createLocalEnvironment();

        // 3、创建集群执行环境
        StreamExecutionEnvironment env4 = StreamExecutionEnvironment.createRemoteEnvironment(
                "hadoop102",    // JobManager主机名
                1234,           // JobManager端口号
                "path/to/jarFile.java"  // 提交给JobManager的jar包
        );
    }

}
