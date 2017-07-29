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
echo Using JAVA_HOME:  $JAVA_HOME

DEBUG_PORT=`expr $1 - 10000`
echo Using DEBUG_PORT: $DEBUG_PORT
JAVA_OPTS="-Dproject.name=dts-all"
JAVA_OPTS="${JAVA_OPTS} -Ddts.type=Server-$1"
JAVA_OPTS="${JAVA_OPTS} -DServerPort=$1"
JAVA_OPTS="${JAVA_OPTS} -server -Xms1024m -Xmx1024m -XX:NewSize=512m -XX:MaxNewSize=512m -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+DoEscapeAnalysis"
JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=n"

for file in $BASE_DIR/lib/*.jar;
do
  CLASSPATH=$CLASSPATH:$file
done

CLASSPATH=$CLASSPATH:$BASE_DIR/conf
CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
echo Using CLASSPATH: $CLASSPATH

INI_FILE=`cygpath --path --windows "${BASE_DIR}/conf/dts.ini"`
echo Using INIFILE: $INI_FILE

java $JAVA_OPTS  -cp $CLASSPATH com.le.dts.server.DtsServer $INI_FILE $1 > $BASE_DIR/logs/start.logger 2>&1 &

echo PID: "$!"
echo "$!" >> $BASE_DIR/bin/dts.pid