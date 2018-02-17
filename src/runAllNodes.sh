#!/bin/bash
cd /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src
find . -name "*.class" -exec rm -f {} \;
javac ringp2p/Initializer.java

eval `ssh-agent`

ssh-add ~/.ssh/id_rsa

start=$SECONDS
ssh -tt jm354@pc5-001-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_root.sh &
ssh -tt jm354@pc5-002-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_non_root.sh &
ssh -tt jm354@pc5-003-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_non_root.sh &
sleep 0.5
ssh -tt jm354@pc5-011-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_last_node.sh &

# ssh -tt jm354@pc5-002-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_non_root.sh &
#sleep 0.5
#ssh -tt jm354@pc5-003-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_non_root.sh &
#sleep 0.5
#ssh -tt jm354@pc5-004-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_non_root.sh &
#sleep 0.5
#ssh -tt jm354@pc5-012-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_non_root.sh &
#sleep 0.5
#ssh -tt jm354@pc5-020-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_non_root.sh &

#ssh -tt jm354@pc5-019-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_last_node.sh &

duration=$(($SECONDS - start))
echo ${duration}


sleep 3
echo "";
ssh pc5-002-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/pg44823.txt" && echo "002 found" || echo "002 not found"
ssh pc5-003-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/pg44823.txt" && echo "003 found" || echo "003 not found"
ssh pc5-011-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/pg44823.txt" && echo "011 found" || echo "011 not found"
#ssh pc5-003-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/pg44823.txt" && echo found || echo not found
#ssh pc5-004-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/pg44823.txt" && echo found || echo not found
#ssh pc5-012-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/pg44823.txt" && echo found || echo not found
#ssh pc5-020-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/pg44823.txt" && echo found || echo not found
#ssh pc5-019-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/pg44823.txt" && echo found || echo not found

echo "";
ssh -tt jm354@pc5-002-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/pg44823.txt' &
ssh -tt jm354@pc5-003-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/pg44823.txt' &
ssh -tt jm354@pc5-011-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/pg44823.txt' &

#ssh -tt jm354@pc5-002-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/pg44823.txt' &
#ssh -tt jm354@pc5-004-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/pg44823.txt' &
#ssh -tt jm354@pc5-012-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/pg44823.txt' &
#ssh -tt jm354@pc5-020-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/pg44823.txt' &
#ssh -tt jm354@pc5-019-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/pg44823.txt' &
exit