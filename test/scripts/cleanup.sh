#!/usr/bin/env bash

arr=("$@")
remotefile="${@: -1}"
rmfile='rm '${remotefile}
killhungprocess="for pid in $(ps -ef | grep "ssh-agent" | awk '{print $2}'); do kill -9 ${pid}; done"


# Cleanup
echo ""
echo "Cleanup"

for i in "${arr[@]}"
do
    ssh ${i} "${rmfile}"
done
echo "";
exit

