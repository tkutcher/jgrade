# JGrade Gradescope Example

This demonstrates a Gradescope autograder that uses checkstyle and JUnit.

[![Watch the video](https://img.youtube.com/vi/o1FHbHZwyUY/maxresdefault.jpg)](https://youtu.be/o1FHbHZwyUY)

These are the files and directories:
* `make_autograder.sh`: zips up files for uploading to Gradescope
* `makefile`: provides an alternative to directly sourcing the shell scripts
* `setup.sh` [required by Gradescope]: sets up the environment by installing a recent JDK
* `run_autograder` [required by Gradescope]: attempts to copy student code into `/autograder/source` directory, compile the grading code with the student code, and run the grader
* `compile.sh`: compiles the combined code, copying `compilation_error.json` into the `results` directory if compilation fails
* `compilation_error.json`: the JSON for indicating the [student's] code did not compile
* `run.sh`: runs JGrade, passing in the assignment-specific grading class (`GradeHello`)
* `README.md`: this file
* `classes`: the destination for compiled files
* `lib/`: the location of needed libraries, which are not checked into git
   * `checkstyle-10.5.0-all.jar` [which you need to download if you want]
   * `jgrade-2.1.1-all.jar` [which you need to build yourself for now]
   * `README.md`: documentation
* `res/`: the location of resources
   * `sun_checks.xml`: a configuration file needed by checkstyle
* `src/main/java/`
   * `staff/hello/`: code provided by the instructor
      * `GradeHello.java`: the controller (package is `staff.hello`)
      * `Greeting.java`: code imported by the student (package is `student.hello`)
      * `Hello.java`: model solution to the assignment (package is `student.hello`)
      * `HelloTest`: test cases using JUnit (package is `staff.hello`)
   * `student/hello/`: the student's code
      * `Hello.java`: skeletal code that students need to complete
* `test_submissions/`: zip files of student submissions, to be manually provided to Gradescope for testing the autograder
   * `correct.zip`: a fully functional project with checkstyle errors
   * `errors.zip`: a project that fails some tests
   * `nocompile.zip`: a project that has compile errors
* `zips/`: where `build_autograder.sh` places the zipped autograder it builds

To test (and debug) the autograder before uploading it, execute:
```
./run_autograder --local
```

To build the autograder, run either `$ sh make_autograder.sh` or `$ make autograder` which will place it in the `zips/` folder.
