#!/usr/bin/env sh
# Builds the project into ./out and runs it.
# Usage: ./run.sh          - interactive console
#        ./run.sh --demo   - scripted demo of all patterns
set -e
cd "$(dirname "$0")"
mkdir -p out
javac --release 17 -encoding UTF-8 -d out -sourcepath src src/com/restaurant/Main.java
java -cp out com.restaurant.Main "$@"
