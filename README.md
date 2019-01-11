
# JGrade
_A library for grading Java assignments_

[![Build Status](https://travis-ci.com/tkutche1/jgrade.svg?token=o33zRRbwCfdkkKhsDNVp&branch=development)](https://travis-ci.com/tkutche1/jgrade)

---

## Overview
JGrade is a helper tool with various classes designed to assist in course instructors "autograding" an assignment, inspired by the (Gradescope Autograder)[TODO]. There are classes that the client can integrate with directly, or use the jar's main method (and provide a class with annotations) that wraps a lot of common functionality (see (examples)[TODO]). It was designed to produce the output needed for Gradescope while being extensible enough to produce different outputs and configure the specific JSON output Gradescope is looking for.


## Building

### Main
The project is setup as a maven project, so if you've installed maven and have cloned this locally then to build the project you just need to run `mvn install` to build the entire project.

### Tests



