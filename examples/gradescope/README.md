# JGrade Gradescope Example

This is a full example that works on gradescope, and models much of the setup from the original [java example](https://github.com/gradescope/autograder_samples/tree/master/java) Gradescope links to.

It compiles all files in to a created `classes/` directory (not tracked). The `lib/` folder contains all jars and library files - for this example just `jgrade-1.0.0-all.jar` (which includes JUnit, etc.), and `checkstyle-8.12.jar`. The `res/` directory is for resources (like the checkstyle configuration file). `src/` is the main source code, and `test_submissions/` are submissions to test with on Gradescope.

The source has 2 main packages, `staff` and `student`. The staff package contains the unit tests, a solution (to debug with) and the code to do the grading.

To build the autograder, run either `$ sh make_autograder.sh` or `$ make autograder` which will place it in the `zips/` folder.

While debugging, a makefile is provided for compiling and running. `make output` will start fresh and run the autograder, pretty-printing the output to the console.

- `setup.sh`: Installs correct JDK
- `run_autograder`: Main script for the autograder. Copies in submission, compiles, and runs.
- `compile.sh`: Compiles all of the source into a classes directory
- `run.sh`: Runs JGrade, passing in the `GradeHello` file, writing output
  - If run with `--local` then prints output to console, else to the results/results.json file.