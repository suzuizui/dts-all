#!/bin/bash
cygwin=false;
linux=false;

case "`uname`" in
    Linux)
                bin_abs_path=$(readlink -f $(dirname $0))
                ;;
        *)
                bin_abs_path=`cd $(dirname $0); pwd`
                ;;
esac

get_pid() {
        STR=$1
        PID=$2
    if $cygwin; then
        JAVA_CMD="$JAVA_HOME\bin\java"
        JAVA_CMD=`cygpath --path --unix $JAVA_CMD`
        JAVA_PID=`ps |grep $JAVA_CMD |awk '{print $1}'`
    else
        if $linux; then
                if [ ! -z "$PID" ]; then
                        JAVA_PID=`ps -C java -f --width 1000|grep "$STR"|grep "$PID"|grep -v grep|awk '{print $2}'`
                    else 
                        JAVA_PID=`ps -C java -f --width 1000|grep "$STR"|grep -v grep|awk '{print $2}'`
                fi
            else
                if [ ! -z "$PID" ]; then
                        JAVA_PID=`ps aux |grep "$STR"|grep "$PID"|grep -v grep|awk '{print $2}'`
                    else 
                        JAVA_PID=`ps aux |grep "$STR"|grep -v grep|awk '{print $2}'`
                fi
            fi
    fi
    echo $JAVA_PID;
}

#pidfile="${bin_abs_path}/dts.pid"
#pids=`jps|grep DtsServer | awk '{print $1}'`
pids=`ps ux|grep 'Dproject.name=dts-all'|grep -v grep|awk '{print $2}'`

if [ "$pids" == "" ];then
        echo "dts is not running. exists"
else
#	pids=`cat $pidfile`
	for pid in $pids
	do
		kill $pid
		LOOPS=0
		while true
		do
			tmpPid=`get_pid "dts" "$pid"`
			if [ "$tmpPid" == "" ]
			then
				echo "stoped[$pid] cost:$LOOPS"
				break;
			fi
			let LOOPS=LOOPS+1
            sleep 1
		done
	done
#	rm -rf $pidfile
fi
