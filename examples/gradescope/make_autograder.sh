#!/usr/bin/env bash

echo "Building Autograder..."

zip -r hello_autograder.zip lib/ res/ src/ compile.sh run.sh setup.sh run_autograder
mv hello_autograder.zip zips/

echo "---"
echo "DONE. Locate it in the zips directory."