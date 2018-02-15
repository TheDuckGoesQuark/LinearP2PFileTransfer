#!/bin/bash
find . -name "*.class" -exec rm -f {} \;
javac ringp2p/Initializer.java
java ringp2p.Initializer root_input.txt
