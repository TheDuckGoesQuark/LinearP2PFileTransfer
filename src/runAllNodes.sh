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
sleep 10 # Avoids final node ending chain early
ssh -tt jm354@pc5-011-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_last_node.sh &
duration=$(($SECONDS - start))

## Print results
sleep 15
echo "";
ssh pc5-002-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/cawiki-20140129-stub-articles.xml" && echo "002 found" || echo "002 not found"
ssh pc5-003-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/cawiki-20140129-stub-articles.xml" && echo "003 found" || echo "003 not found"
ssh pc5-011-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/cawiki-20140129-stub-articles.xml" && echo "011 found" || echo "011 not found"
echo ${duration}
echo "";
ssh -tt jm354@pc5-002-l.cs.st-andrews.ac.uk 'rm /cs/scratch/cawiki-20140129-stub-articles.xml' &
ssh -tt jm354@pc5-003-l.cs.st-andrews.ac.uk 'rm /cs/scratch/cawiki-20140129-stub-articles.xml' &
ssh -tt jm354@pc5-011-l.cs.st-andrews.ac.uk 'rm /cs/scratch/cawiki-20140129-stub-articles.xml' &
exit