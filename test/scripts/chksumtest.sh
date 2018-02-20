#!/usr/bin/env bash
localpath="/cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/test/test_data/"
localfile=${localpath}"pg44823.txt"
remotepath="/cs/scratch/jm354/"
remotefile=${remotepath}"pg44823.txt"

declare -a arr=(
"jm354@pc2-105-l.cs.st-andrews.ac.uk"


)

#"jm354@pc2-112-l.cs.st-andrews.ac.uk"
#"jm354@pc2-099-l.cs.st-andrews.ac.uk"
#"jm354@pc2-085-l.cs.st-andrews.ac.uk"
#"jm354@pc2-071-l.cs.st-andrews.ac.uk"
#"jm354@pc2-075-l.cs.st-andrews.ac.uk"
#"jm354@pc2-067-l.cs.st-andrews.ac.uk"
#"jm354@pc2-025-l.cs.st-andrews.ac.uk"

eval `ssh-agent`
ssh-add ~/.ssh/id_rsa

for i in "${arr[@]}";do
res1=$(date +%s.%N)

`ssh ${i} "[[ -f ${remotefile} ]] && cksum ${remotefile} | grep -o '^\S*'"`

res2=$(date +%s.%N)
dt=$(echo "$res2 - $res1" | bc)
dd=$(echo "$dt/86400" | bc)
dt2=$(echo "$dt-86400*$dd" | bc)
dh=$(echo "$dt2/3600" | bc)
dt3=$(echo "$dt2-3600*$dh" | bc)
dm=$(echo "$dt3/60" | bc)
ds=$(echo "$dt3-60*$dm" | bc)

printf "Runtime: %02.4f\n" ${ds}
done