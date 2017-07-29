#!/bin/bash

pids=`ps ux|grep 'Dproject.name=dts-all'|grep -v grep|awk '{print $2}'`

if [ "$pids" == "" ];then
        echo "1"
else
		echo "0"
fi