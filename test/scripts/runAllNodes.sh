#!/bin/bash

localpath="/cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/test/test_data/"
localfile=${localpath}"pg44823.txt"
remotepath="/cs/scratch/jm354/"
remotefile=${remotepath}"pg44823.txt"
rootScript="/cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/test/scripts/run_root.sh"
nonRootScript="/cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/test/scripts/run_non_root.sh"
checksum=$(cksum "${localfile}" | grep -o '^\S*')


root="jm354@pc2-009-l.cs.st-andrews.ac.uk"
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






# Begin timer
res1=$(date +%s.%N)

# Run root node
ssh ${root} "${rootScript} ${localfile} ${#arr[@]}"  &

# Run nodes
for i in "${arr[@]}"
do
    ssh ${i} "${nonRootScript} ${remotepath}  "  &
done

# Compare checksum of remote and origin files
echo ""
for i in "${arr[@]}"
do
  while true; do
    val=`ssh ${i} "[[ -f ${remotefile} ]] && cksum ${remotefile} | grep -o '^\S*'"`
    if [[ "${checksum}" == "${val}" ]]; then
      break
    fi
    sleep 1
  done
done

## Get runtime accurately
res2=$(date +%s.%N)
dt=$(echo "$res2 - $res1" | bc)
dd=$(echo "$dt/86400" | bc)
dt2=$(echo "$dt-86400*$dd" | bc)
dh=$(echo "$dt2/3600" | bc)
dt3=$(echo "$dt2-3600*$dh" | bc)
dm=$(echo "$dt3/60" | bc)
ds=$(echo "$dt3-60*$dm" | bc)

printf "Runtime: %02d:%02.4f\n" ${dm} ${ds}

/cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/test/scripts/cleanup.sh "${arr[@]}" ${remotefile} > /dev/null 2>&1

exit