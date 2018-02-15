#!/bin/bash
find . -name "*.class" -exec rm -f {} \;
javac ringp2p/Initializer.java

trap 'eval "$(ssh-agent -s -k)"' EXIT
eval "$(ssh-agent -s)"
if ssh-add; then
    ssh jm354@pc5-004-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_root.sh &
    ssh jm354@pc5-003-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_non_root.sh &
    ssh jm354@pc5-002-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/run_last_node.sh &
fi



