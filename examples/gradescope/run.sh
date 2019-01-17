#!/usr/bin/env bash

# make cwd the compiled class files
cd classes

# invoke the main program of JGrade passing the class GradeHello as the main parameter
# sending output to the results.json file, pretty-printing for demo purposes.
# switch these if putting on gradescope.

# local
java -jar ../lib/jgrade-1.0.0-alpha-all.jar -c staff.hello.GradeHello --pretty-print

# on gradescope
# java -jar ../lib/jgrade-1.0.0-alpha-all.jar -c staff.hello.GradeHello -o /autograder/results/results.json

# return to original cwd
cd ..