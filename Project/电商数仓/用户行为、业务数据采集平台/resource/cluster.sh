#!/bin/bash

case $1 in
"start") {
	echo "=======================启动用户行为日志采集======================="

	# 启动zookeeper
	zk.sh start

	# 启动Hadoop集群
	myhadoop.sh start

	# 启动Kafka集群
	kf.sh start

	# 启动Flume第一条通道
	f1.sh start

	# 启动Flume第二条通道
	f2.sh start
};;
"stop") {
	echo "=======================停止用户行为日志采集======================="

	#停止 Flume消费集群
	f2.sh stop

	#停止 Flume采集集群
	f1.sh stop

	#停止 Kafka采集集群
	kf.sh stop

	#停止 Hadoop集群
	myhadoop.sh stop

	#停止 Zookeeper集群
	zk.sh stop
};;
*) {
	echo "Input Args Error..."
};;
esac