#!/usr/bin/env bash

echo "Removing carriage returns from scripts..."
dos2unix *.sh

echo "Building Autograder..."

zip -r hello_autograder.zip lib/ res/ src compile.sh run.sh setup.sh run_autograder compilation_error.json
mv hello_autograder.zip zips/

echo "---"
echo "DONE. Locate it in the zips directory."
