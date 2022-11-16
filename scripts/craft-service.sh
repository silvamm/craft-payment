#!/bin/sh
PATH_TO_JAR=$(find . -iname '*.jar' -type f)
filename=$(basename -- "$PATH_TO_JAR")
SERVICE_NAME="${filename%.*}"
PID_PATH_NAME=/tmp/$( echo $SERVICE_NAME )

start() {
    echo "$SERVICE_NAME starting..."
    if [ ! -f $PID_PATH_NAME ]; then
        nohup java -Xms1024m -jar $PATH_TO_JAR >> myService.out 2>&1&
                    echo $! > $PID_PATH_NAME


        echo "$SERVICE_NAME started ..."
    else
        echo "$SERVICE_NAME is already running ..."
    fi
}

stop() {
    if [ -f $PID_PATH_NAME ]; then
        PID=$(cat $PID_PATH_NAME);
        echo "$SERVICE_NAME stoping ..."
        kill $PID;
        echo "$SERVICE_NAME stopped ..."
        rm $PID_PATH_NAME
    else
        echo "$SERVICE_NAME is not running ..."
    fi
}

status() {
    if [ -f $PID_PATH_NAME ]; then
        PID=$(cat $PID_PATH_NAME);
        echo "$SERVICE_NAME is running with PID: $PID"
    else
        echo "$SERVICE_NAME is not running ..."
    fi
}


case $1 in
    start)
        start
    ;;
    stop)
        stop
    ;;
    status)
        status
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            stop
            start
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
esac