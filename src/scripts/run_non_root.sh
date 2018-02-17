#!/bin/bash
wait_file() {
  local file="$1"; shift
  local wait_seconds="${1:-10}"; shift # 10 seconds as default timeout

  until test $((wait_seconds--)) -eq 0 -o -f "$file" ; do sleep 1; done

  ((++wait_seconds))
}

cd /cs/home/jm354/Documents/ThirdYear/Networking/FileTransfer/src
java ringp2p.Initializer non_root_input.txt &
wait_file "/cs/scratch/jm354/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4" 20 || {
    exit 1
}