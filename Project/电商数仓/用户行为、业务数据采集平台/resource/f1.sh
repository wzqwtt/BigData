#!/bin/bash

case $1 in
"start") {
	for i in hadoop102 hadoop103
	do
		echo "-------------------启动 $i 采集flume-------------------"
		ssh $i "nohup /opt/module/flume-1.9.0/bin/flume-ng agent -n a1 -c /opt/module/flume-1.9.0/conf/ -f /opt/module/flume-1.9.0/job/project/file_to_kafka.conf >/dev/null 2>&1 &"
	done
};;
"stop") {
	for i in hadoop102 hadoop103
	do
		echo "-------------------停止 $i 采集flume-------------------"
		ssh $i "ps -ef | grep file_to_kafka.conf | grep -v grep |awk  '{print \$2}' | xargs -n1 kill -9 "
	done
};;
*) {
	echo "Input Args Error..."
};;
esac