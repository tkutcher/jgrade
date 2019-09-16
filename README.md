
# JGrade
_A library for grading Java assignments_

[![Build Status](https://travis-ci.com/tkutcher/jgrade.svg?token=o33zRRbwCfdkkKhsDNVp&branch=development)](https://travis-ci.com/tkutcher/jgrade)

- [Overview](#overview)
  - [Library Files](#library-files)
  - [Discussion](#inspiration-and-discussion)
  - [Acknowledgements](#acknowledgements)
- [Use](#use)
- [Building](#building)
  - [JGrade](#building-jgrade)
  - [Tests](#running-tests)
  - [Requirements](#requirements)
  - [Full API](#full-api)
  
  
---

## Overview
JGrade is a helper tool with various classes designed to assist in course instructors "autograding" an assignment, inspired by the [Gradescope Autograder](https://gradescope-autograders.readthedocs.io/en/latest/). There are classes that the client can integrate with directly, or use the jar's main method (and provide a class with annotations) that wraps a lot of common functionality (see [examples](https://github.com/tkutche1/jgrade/tree/development/examples)). It was designed to produce the output needed for Gradescope while being extensible enough to produce different outputs and configure the specific JSON output Gradescope is looking for.

### Library Files

- `jgrade` package
  - `gradedtest` package
    - A package specific to the `GradedTest` related objects
    - `GradedTest`: An annotation for unit tests that are "graded"
    - `GradedTestListener`: An extension of the JUnit `RunListener` that looks for `GradedTest` annotations in a run and creates `GradedTestResult`s.
    - `GradedTestResult`: An object for a specific instance of a graded test (holding the score, output, etc. for that test)
  - `gradescope` package
    - `GradescopeJsonObserver`: An observer for a `Grader` that produces JSON output according to the [Gradescope Autograder Specifications](https://gradescope-autograders.readthedocs.io/en/latest/specs/)
    - `GradescopeJsonException`: A `RuntimeException` for when the JSON to output does not meet the requirements for Gradescope.
  - `AfterGrading`: An annotation run after `Grade` annotated methods.
  - `BeforeGrading`: An annotation run before `Grade` annotated methods.
  - `Grade`: An annotation for anything to do to grade.
  - `Grader`: An object that holds data specific to general purpose grading (a list of `GradedTestResult`s, output for the whole run, etc.)
  - `GraderObserver`: Just an observer interface to observe a `Grader` and produce output.
  - `GraderStrategy`: Strategy interface for anything to do to modify `GradedTestResults` that were run as part of a JUnit suite. For example, this contains a `DeductiveGraderStrategy` to treat them as grading deductively from some max score down to some floor.
  - `CheckstyleGrader`: A class to assist in running checkstyle for a `GradedTestResult`.
  - `CLITester`: An abstract class to assist in running the main method of programs with a command line interface for output.
  - `CLIResult`: An interface for an object to hold multiple streams of captured output from the run.

  
### Inspiration and Discussion
First, I wanted to have a setup for making Gradescope Autograders that was easily configurable so that as assignments change/update it is easy to modify the code related to testing the student's code. For example, if one assignment could benefit from a leaderboard/execution-time-statistics it would be easy to extend in to the same code for other assignments that don't need that information. I already had most of the code written and I had a few free days so this repository is just the repackaging of that code in to a main-program-executable/library.

Secondly, there were a few cases that came up where it was easier to manually add the   `GradedTestResult` somewhere rather than have all tests be a simple pass/fail of a unit test. For example, say you deduct 1 point for each checkstyle error and you want to incorporate that in the autograded portion, it would be much easier to have the ability to run checkstyle outside of a unit test (since a unit test would be a simple yes/no result) and just add that test result to the encapsulating `Grader` object. I also find it much easier to have something observe a `Grader` object rather than have specific `RunListener`'s that produce each type of output.

The way I use this library is to have a base class for the course (for example, a `_226Grader`) that contains annotated methods for functionality/grading parts that are consistent across all assignments. For example, the `@BeforeGrading` method starts a timer and the `@AfterGrading` method stops it. There is a `@Grade` method that does the "grading" of style with checkstyle. Subclasses, for example `Assignment1Grader`, extend this and add `@Grade` methods to add assignment-specific grading. See the gradescope folder in the examples for a rough example setup.

The way the main method works with annotations and all may be a bit funky since it passes the same `Grader` object to each method. It essentially acts like a singleton, but conceivably (if there was some application to grade multiple students at once) you may want multiple instances of it so it doesn't actually follow a singleton design pattern. Secondly, there could be some value down the road where each `@Grade` method is run independently of the others (maybe if an instructor is grading the student in their own IDE or something).

I haven't written anything yet to produce anything other than the Gradescope JSON output, but I could see how it would be useful to have various output formats and the way it is set up makes it easy to add that.

Lastly, I'm sure this could benefit from a collaborative effort! Especially since I imagine a lot of people are writing lots of similar code to achieve the same goals.
  
### Acknowledgements
This code is essentially a repackaging of the code developed for JHU's Data Structures course in the Fall 2018 semester. That code started from the [jh61b](https://github.com/gradescope/autograder_samples/tree/master/java/src/main/java/com/gradescope/jh61b) package that Gradescope directs to in it's Autograder page. Some things are left from this package (like the `GradedTest` annotation and some of the `GradedTestListener` code) but with some modifications.

When repackaging and making a jar that can be run as an executable, I modeled Professor Peter Froehlich's [Jaybee Project](https://github.com/phf/jb). I used much of the same approach and borrowed the code for the reflection functionality.

---

## Use

<!-- TODO - Move a lot of this to Wiki -->

The [examples](https://github.com/tkutche1/jgrade/tree/development/examples) directory highlights most of the core functionality. There are two ways to utilize this library.

First, the client can instantiate/extend their own `Grader` (which is a public class). They can implement then attach their own observers (or just use the provided `GradescopeJsonObserver`) and produce their desired output. They can use whatever other `Grader` methods as desired.

Secondly, the client can run the main method in the `JGrade` class (which is the main method for the entire jar). The command line expects the name of a class containing `@Grade` methods and optional format parameters. The usage message for this main program can be expanded below:
<details><summary>Usage</summary>
<p>

```
-c,--classname arg            the class containing annotated methods to grade
-f,--format output-format     specify output, one of 'json' (default) or 'txt'
-h,--help<br>
   --no-output                don't produce any output (if user overriding)
-o destination                save output to another file (if not specified,
                              prints to standard out)
   --pretty-print             pretty-print output (when format is json)
-v,--version

```

</p>
</details>


With a `Grader` object, the client can do the following:
- `attachOutputObserver(OutputObserver o)`: Attach an output observer to interpret the data the `Grader` holds
- `notifyOutputObservers()`: Let all observers know that the data has changed
- `startTimer()` and `stopTimer()`: Time certain execution when grading
- `runJUnitGradedTests(Class testSuite)`: Run all unit tests in class `testSuite` and add `GradedTestResult`s for each `@GradedTest` method in `testSuite`.
  - For example, if class `MyUnitTests` has JUnit `@Test` methods also annotated with `@GradedTest`, and a client has a `Grader g`, then the client can call `g.runJUnitGradedTests(MyUnitTests.class)` and all `GradedTestResult`s will be added to `g`.
  - It works by adding a `GradedTestListener` to a `new JUnitCore()` and running it on the class passed to the method.
- Accessors for the following fields:
  - `score: double`
  - `maxScore: double`
  - `executionTime: long` (automatically set by the timer if used)
  - `output: String` (call `addOutput(String s)` to continually append output)
  - `gradedTestResults: List<GradedTestResult>` (call `addGradedTestResult(GradedTestResult r)`) to add more `GradedTestResult`s)

For the abstract `CLITester` class:
- A client can either extend it and write unit tests for functionality in a base class, or just use it's static methods called `executeProcess`
  - These methods return a `CLIResult`, where a client can call `getOutput()` or `getOutputByLine()` passing the enumerated `STREAM`
  - The stream is one of `STREAM.STDOUT` or `STREAM.STDERR`.
- If extending the class
  - The client must implement abstract method `getInvocation()`
  - This should return the List of strings that invoke the process for each run in the unit test suite
    - e.g. `"java", "-cp", "lib/:.", "com.blah.mypackage.Hello"`
    - Then, if a client wanted to add command line arguments for a specific test method, within that method the client could do `getCommand().append("someArgument");`
    - Note you __cannot__ include any IO redirection in this command, instead you call `runCommand()` with the input that you want to feed in, and use the `CLIResult` to get the output as a String.
    - For now, there is no `runCommand(File)` method for running with input, only `runCommand(String)`.
    - It is overloaded so no parameter means the program expects no input.

For the `CheckstyleGrader`:
- Construct it with the total points to allocate for it, the amount to deduct per error, the path to the checkstyle jar, and the directory containing files to check.
- If using a config file, call `setConfig` with the path to the xml.
- `runForGradedTestResult()` will return a `GradedTestResult` holding the information from the checks.
- Right now, the default is to run checkstyle for all java files in some directory (the submission directory), excluding any with "test" in their path.
  - If a client wanted to manually configure this, they would need to subclass `CheckstyleGrader` and override the `isFileToCheck` method
   
_For a more detailed API description, see how to acquire the [full API](#full-api)_

---

## Building

### Building JGrade

The jar can be downloaded directly from the [Releases](https://github.com/tkutche1/jgrade/releases).

Alternatively, you can clone the repository and build it yourself. The project is setup as a maven project, so if you've installed maven and have cloned this locally then to build the project you just need to run `mvn install` to build the entire project.

Note that `jgrade-X.X.X.jar` does not include the other dependencies for the project while `jgrade-X.X.X-all.jar` does. If running as an executable, you'd need all dependencies anyways. If the only thing you use is the `Grader` object and some JUnit stuff, and you have your own `OutputObserver`, then there's no need to include all dependencies.

### Running Tests
Similarly, to run the unit tests for this just run `mvn test`. There is also a class in the jgrade package (in the [tests](https://github.com/tkutche1/jgrade/tree/development/src/test/java/edu/jhu/cs/jgrade) directory of this repository named `AllJGradeTests` - this class is a suite of all unit tests for the program. Note that to test the `GradedTestListener` class there are some dummy unit test inner classes that are not actual tests (many of which fail intentionally).

### Requirements
JGrade is written in [Java 8](https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html). Since the library has classes designed to run alongside JUnit, [JUnit 4](https://junit.org/junit4/) is a dependency for the entire project (as opposed to just running the projects own unit tests). The [org.json](https://mvnrepository.com/artifact/org.json/json) package is used in producing correctly formatted JSON output, and the [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/) library is used for reading the command line in the main program.

For simplicity, the main jar (appended with "-all") includes all of these dependencies.

### Full API
The full API (generated from the Javadoc comments) can be retrieved from the [Releases](https://github.com/tkutche1/jgrade/releases) page as well. The pom also uses the maven javadoc plugin so to build this yourself you could run `mvn javadoc:jar`
