#!/bin/bash

PROG_NAME=$0
ACTION=$1

usage() {
    echo "Usage: $PROG_NAME {start|stop|pubstart|restart|deploy}"
    exit 1;
}

if [ "$UID" -eq 0 ]; then
    echo "can't run as root, please use: sudo -u admin $0 $@"
    exit 1
fi

if [ $# -lt 1 ]; then
    usage
fi

HOME="$(getent passwd "$UID" | awk -F":" '{print $6}')" # fix "$HOME" by "$UID"
APP_HOME=$(cd $(dirname $0)/..; pwd)
APP_NAME=$(basename "${APP_HOME}")
APP_PKG=$APP_HOME/target/$APP_NAME.tgz
APP_RUN=$APP_HOME/target/$APP_NAME-run
APP_BIN=$APP_RUN/bin
APP_START=$APP_BIN/run.sh
APP_STOP=$APP_BIN/stop.sh
APP_CHECK=$APP_BIN/check.sh

# os env
# NOTE: must edit LANG and JAVA_FILE_ENCODING together
export LANG=zh_CN.GB18030
export JAVA_FILE_ENCODING=GB18030
export NLS_LANG=AMERICAN_AMERICA.ZHS16GBK
export JAVA_HOME=/opt/taobao/java
export CPU_COUNT="$(grep -c 'cpu[0-9][0-9]*' /proc/stat)"

export APP_START_LOG=$APP_HOME/logs/app_start.log

source "$APP_HOME/bin/hook.sh"

start() {
    # update app package
    if [ -f "$APP_PKG" ];then
        echo "extract $APP_PKG"
        rm -rf $APP_RUN || exit
        tar xzf $APP_PKG -C $APP_HOME/target || exit
        if [ ! -d "$APP_RUN" ];then
            echo "ERROR: $APP_RUN doesn't exist."
            exit 2
        fi
    else
        echo "ERROR: $APP_PKG doesn't exist."
        exit 2
    fi

    # delete old $APP_START_LOG, keep last 20 logs
    ls "$APP_START_LOG".* 2>/dev/null | tail -n +$((20 + 1)) | xargs --no-run-if-empty rm -f
    if [ -e "$APP_START_LOG" ]; then
        mv "$APP_START_LOG" "$APP_START_LOG.$(date '+%Y%m%d%H%M%S')" || exit
    fi
    mkdir -p "$(dirname "${APP_START_LOG}")" || exit
    touch "$APP_START_LOG" || exit

    # show locale
    locale >> ${APP_START_LOG}

    mkdir -p ${APP_HOME}/target || exit
    mkdir -p ${APP_HOME}/logs || exit

    beforeStartApp
    stop
    # async start
    for i in `seq 52014 52016`
    do
        bash $APP_START $i >> ${APP_START_LOG} 2>&1 &
    done
}

stop() {
    echo "stop app"
    beforeStopApp
    bash $APP_STOP >> ${APP_START_LOG} 2>&1 &
    afterStopApp
}

start_http() {
    sleep 10
    bash $APP_CHECK
    if [ "$?" -eq 0 ]; then
        echo "app start success"
    else
        echo "app start failed, please fix the problem then restart"
        exit 1
    fi
    afterStartApp
}

case "$ACTION" in
    start)
        start
    ;;
    stop)
        stop
    ;;
    pubstart)
        stop
        start
        start_http
    ;;
    online)
        online
    ;;
    offline)
        offline
    ;;
    restart)
        stop
        start
        start_http
    ;;
    deploy)
        stop
        start
        start_http
        backup
    ;;
    *)
        usage
    ;;
esac

