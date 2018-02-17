#!/bin/bash
cd /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src
find . -name "*.class" -exec rm -f {} \;
javac ringp2p/Initializer.java

eval `ssh-agent`

ssh-add ~/.ssh/id_rsa

start=$SECONDS
ssh -tt jm354@pc5-001-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_root.sh &
ssh -tt jm354@pc5-002-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_non_root.sh &
ssh -tt jm354@pc5-003-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_non_root.sh &
ssh -tt jm354@pc5-004-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_non_root.sh &
ssh -tt jm354@pc5-005-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_non_root.sh &
ssh -tt jm354@pc5-006-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_non_root.sh &
ssh -tt jm354@pc5-007-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_non_root.sh &
ssh -tt jm354@pc5-008-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_non_root.sh &
ssh -tt jm354@pc5-009-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_non_root.sh &

sleep 15 # Avoids final node ending chain early
ssh -tt jm354@pc5-011-l.cs.st-andrews.ac.uk 'bash -s' < /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src/scripts/run_last_node.sh &
duration=$(($SECONDS - start))

## Print results
sleep 15
echo "";
ssh pc5-002-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4" && echo "002 found" || echo "002 not found"
ssh pc5-003-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4" && echo "003 found" || echo "003 not found"
ssh pc5-004-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4" && echo "004 found" || echo "004 not found"
ssh pc5-005-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4" && echo "005 found" || echo "005 not found"
ssh pc5-006-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4" && echo "012 found" || echo "012 not found"
ssh pc5-007-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4" && echo "017 found" || echo "017 not found"
ssh pc5-008-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4" && echo "018 found" || echo "018 not found"
ssh pc5-009-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4" && echo "019 found" || echo "019 not found"
ssh pc5-011-l.cs.st-andrews.ac.uk test -f "/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4" && echo "011 found" || echo "011 not found"

echo ${duration}
echo "";
ssh -tt jm354@pc5-002-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4' &
ssh -tt jm354@pc5-003-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4' &
ssh -tt jm354@pc5-004-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4' &
ssh -tt jm354@pc5-005-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4' &
ssh -tt jm354@pc5-006-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4' &
ssh -tt jm354@pc5-007-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4' &
ssh -tt jm354@pc5-008-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4' &
ssh -tt jm354@pc5-009-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4' &
ssh -tt jm354@pc5-011-l.cs.st-andrews.ac.uk 'rm /cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4' &
exit