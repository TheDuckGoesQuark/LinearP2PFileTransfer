#!/bin/bash
## Recompile
cd /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src
find . -name "*.class" -exec rm -f {} \;
javac ringp2p/Initializer.java

localfile="/cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/test/test_data/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4"
remotepath="/cs/scratch/jm354/"
remotefile="/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4"
rootScript="/cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/test/scripts/run_root.sh"
nonRootScript="/cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/test/scripts/run_non_root.sh"
checksum=$(cksum "${localfile}" | grep -o '^\S*')


root="jm354@pc2-009-l.cs.st-andrews.ac.uk"
declare -a arr=(
"jm354@pc2-105-l.cs.st-andrews.ac.uk"
"jm354@pc2-075-l.cs.st-andrews.ac.uk"
"jm354@pc2-067-l.cs.st-andrews.ac.uk"
"jm354@pc2-025-l.cs.st-andrews.ac.uk"
"jm354@pc2-112-l.cs.st-andrews.ac.uk"
"jm354@pc2-099-l.cs.st-andrews.ac.uk"
"jm354@pc2-085-l.cs.st-andrews.ac.uk"
"jm354@pc2-071-l.cs.st-andrews.ac.uk"
)


eval `ssh-agent`
ssh-add ~/.ssh/id_rsa

# Begin timer
res1=$(date +%s.%N)

# Run root node
ssh ${root} "${rootScript} ${localfile}" "${#arr[@]}" &

# Run nodes
for i in "${arr[@]}"
do
    echo "${i} is started"
    ssh ${i} "${nonRootScript} ${remotepath}" &
done

# Compare checksum of remote and origin files
echo ""
for i in "${arr[@]}"
do
  while true; do
    val=`ssh ${i} "cksum ${remotefile} | grep -o '^\S*'"`
    if [[ "${checksum}" == "${val}" ]]; then
      break
    fi
    sleep 5
  done
done

## Get runtime
res2=$(date +%s.%N)
dt=$(echo "$res2 - $res1" | bc)
dd=$(echo "$dt/86400" | bc)
dt2=$(echo "$dt-86400*$dd" | bc)
dh=$(echo "$dt2/3600" | bc)
dt3=$(echo "$dt2-3600*$dh" | bc)
dm=$(echo "$dt3/60" | bc)
ds=$(echo "$dt3-60*$dm" | bc)

printf "Total runtime: %d:%02d:%02d:%02.4f\n" ${dd} ${dh} ${dm} ${ds}

/cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/test/scripts/cleanup.sh "${arr[@]}" ${remotefile}

for pid in $(ps -ef | grep "ssh-agent" | awk '{print $2}'); do kill -9 ${pid}; done
exit