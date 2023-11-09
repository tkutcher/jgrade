#!/usr/bin/env bash

# start with a clean classes/ directory
rm -rf classes
echo "making clean classes/ directory"
mkdir -p classes

# Compile all java files in src directory
java_files=$(find src -name "*.java")
echo "compiling java files..."
javac -cp lib/jgrade-2.2.0-all.jar:. -d classes ${java_files}
if [ $? -eq 0 ]; then
       echo "---"
       echo "DONE"
       exit 0 # success
else
       echo "Compilation failed"
       if [ "$1" = "--local" ]; then
           cat compilation_error.json
       else
	         cp compilation_error.json /autograder/results/results.json
       fi
       exit 1 # failure
fi
