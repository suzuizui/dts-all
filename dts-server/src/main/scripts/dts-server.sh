#!/bin/bash
#if [ `whoami` == "root" ]; then
#  echo DO NOT use root user to launch me.
#  exit 1;
#fi

cd `dirname $0`/..
BASE_DIR="`pwd`"

echo "listenerPort:"$1

#JAVA_HOME=/opt/taobao/java/
echo Using BASE_DIR:   $BASE_DIR
#echo Using JAVA_HOME:  $JAVA_HOME

DEBUG_PORT=`expr $1 - 10000`
echo $DEBUG_PORT
JAVA_OPTS="-Dproject.name=dts-all"
JAVA_OPTS="${JAVA_OPTS} -Ddts.type=Server-$1"
JAVA_OPTS="${JAVA_OPTS} -DServerPort=$1"
JAVA_OPTS="${JAVA_OPTS} -server -Xms5120m -Xmx5120m -XX:NewSize=1706m -XX:MaxNewSize=1706m -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+DoEscapeAnalysis"
JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=n"

for file in $BASE_DIR/lib/*.jar;
do
  CLASSPATH=$CLASSPATH:$file
done

CLASSPATH=$CLASSPATH:$BASE_DIR/conf

java $JAVA_OPTS  -cp $CLASSPATH com.alibaba.dts.server.DtsServer ${BASE_DIR}/conf/dts.ini $1 > $BASE_DIR/logs/start.logger 2>&1 &

#echo "$!" >> $BASE_DIR/bin/dts.pid