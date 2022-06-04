资源文件介绍：

- 模拟生成用户行为日志jar包
- `flume-interceptor`：用户行为日志采集拦截器
- `file_to_kafka.conf`：从文件到kakfa flume配置文件
- `kafka_to_hdfs.conf`：从kafka到hdfs flume配置文件
- `f1.sh`：从文件到kakfa 执行脚本
- `f2.sh`：从kafka到hdfs执行脚本
- `cluster.sh`：用户行为日志采集启停脚本
- `gmall.sql`：用户业务数据数据库结构脚本
- 模拟生成业务数据jar包
- `maxwell-1.29.2.tar.gz`：尚硅谷特制版maxwell，增加了**日期**参数，方便做这个项目，真正做项目请到Maxwell官网下载安装包
- `mxw.sh`：Maxwell启停脚本