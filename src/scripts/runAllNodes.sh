#!/bin/bash
cd /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src
find . -name "*.class" -exec rm -f {} \;
javac ringp2p/Initializer.java

root="jm354@pc2-009-l.cs.st-andrews.ac.uk"
end="jm354@pc2-063-l.cs.st-andrews.ac.uk"
declare -a arr=(
"jm354@pc2-105-l.cs.st-andrews.ac.uk"
"jm354@pc2-075-l.cs.st-andrews.ac.uk"
"jm354@pc2-067-l.cs.st-andrews.ac.uk"
"jm354@pc2-025-l.cs.st-andrews.ac.uk"
"jm354@pc2-112-l.cs.st-andrews.ac.uk"
"jm354@pc2-099-l.cs.st-andrews.ac.uk"
"jm354@pc2-009-l.cs.st-andrews.ac.uk"
"jm354@pc2-085-l.cs.st-andrews.ac.uk"
)

eval `ssh-agent`
ssh-add ~/.ssh/id_rsa

# Begin timer
res1=$(date +%s.%N)

# Run root node
ssh ${root} 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_root.sh 2> /dev/null &

# Run middle nodes
for i in "${arr[@]}"
do
    echo "${i} is started"
    ssh ${i} 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_non_root.sh 2> /dev/null &
done

# Keep checking for all files being received
echo ""
for i in "${arr[@]}"
do
  while true; do
    str=`ssh ${i} test -f "/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4" && echo "$i found" || echo "$i not found"`
    echo Output: $str
    if [[ ! $str =~ "not" ]]; then
      break
    fi
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

# run last node
ssh ${end} 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_last_node.sh & 2> /dev/null &

printf "Total runtime: %d:%02d:%02d:%02.4f\n" $dd $dh $dm $ds

## Print results
sleep 10


## Cleanup
for i in "${arr[@]}"
do
    ssh -tt ${i} 'rm /cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4' &
done
echo "";
exit