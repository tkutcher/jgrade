
# JGrade
_A library for grading Java assignments_

[![Build Status](https://travis-ci.com/tkutche1/jgrade.svg?token=o33zRRbwCfdkkKhsDNVp&branch=development)](https://travis-ci.com/tkutche1/jgrade)

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
  - `OutputObserver`: Just an observer interface to observe a `Grader` and produce output.

  
### Inspiration and Discussion
First, I wanted to have a setup for making Gradescope Autograders that was easily configurable so that as assignments change/update it is easy to modify the code related to testing the student's code. For example, if one assignment could benefit from a leaderboard/execution-time-statistics it would be easy to extend in to the same code for other assignments that don't need that information. I already had most of the code written and I had a few free days so this repository is just the repackaging of that code in to a main-program-executable/library.

Secondly, there were a few cases that came up where it was easier to manually add the   `GradedTestResult` somewhere rather than have all tests be a simple pass/fail of a unit test. For example, say you deduct 1 point for each checkstyle error and you want to incorporate that in the autograded portion, it would be much easier to have the ability to run checkstyle outside of a unit test and just add that test result to the encapsulating `Grader` object. I also find it much easier to have something observe a `Grader` object rather than have specific `RunListener`'s that produce each type of output.

The way I use this library is to have a base class for the course (for example, a `_226Grader`) that contains annotated methods for functionality/grading parts that are consistent across all assignments. For example, the `@BeforeGrading` method starts a timer and the `@AfterGrading` method stops it. There is a `@Grade` method that does the "grading" of style with checkstyle. Subclasses, for example `Assignment1Grader`, extend this and add `@Grade` methods to add assignment-specific grading.

The way the main method works with annotations and all may be a bit funky since it passes the same `Grader` object to each method. It essentially acts like a singleton, but conceivably (if there was some application to grade multiple students at once) you may want multiple instances of it so it doesn't actually follow a singleton design pattern. Secondly, there could be some value down the road where each `@Grade` method is run independently of the others (maybe if an instructor is grading the student in their own IDE or something).

I haven't written anything yet to produce anything other than the Gradescope JSON output, but I could see how it would be useful to have various output formats and the way it is set up makes it easy to add that.

Lastly, I'm sure this could benefit from a collaborative effort! Especially since I imagine a lot of people are writing lots of similar code to produce the same output.
  
### Acknowledgements
This code is essentially a repackaging of the code developed for JHU's Data Structures course in the Fall 2018 semester. That code started from the [jh61b](https://github.com/gradescope/autograder_samples/tree/master/java/src/main/java/com/gradescope/jh61b) package that Gradescope directs to in it's Autograder page. Some things are left from this package (like the `GradedTest` annotation and some of the `GradedTestListener` code) but with some modifications.

When repackaging and making a jar that can be run as an executable, I modeled Professor Peter Froehlich's [Jaybee Project](https://github.com/phf/jb). I used much of the same approach and borrowed the code for the reflection functionality.

## Use

The [examples](https://github.com/tkutche1/jgrade/tree/development/examples) directory highlights most of the core functionality. There are two ways to utilize this library.

First, the client can instantiate/extend their own `Grader` (which is a public class). They can implement then attach their own observers (or just use the provided `GradescopeJsonObserver`) and produce their desired output. They can use whatever other `Grader` methods as desired.

Secondly, the client can run the main method in the `JGrade` class (which is the main method for the entire jar). The command line expects the name of a class containing `@Grade` methods and optional format parameters. The usage message for this main program can be expanded below:
<details><summary>Usage</summary>
<p>
-c,--classname arg            the class containing annotated methods to grade
-f,--format output-format     specify output, one of 'json' (default) or 'txt'
-h,--help<br>
   --no-output                don't produce any output (if user overriding)
-o destination                save output to another file (if not specified,
                              prints to standard out)
   --pretty-print             pretty-print output (when format is json)
-v,--version<br>
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
  - `gradedTestResults: List<GradedTestResult>` (call `addGradedTestResult(GradedTestResult r))` to add more `GradedTestResult`s)

_For a more detailed API description, see how to acquire the [full API](#full-api)_

## Building

### Building JGrade

The jar can be downloaded directly from the [Releases](https://github.com/tkutche1/jgrade/releases).

Alternatively, you can clone the repository and build it yourself. The project is setup as a maven project, so if you've installed maven and have cloned this locally then to build the project you just need to run `mvn install` to build the entire project.

### Running Tests
Similarly, to run the unit tests for this just run `mvn test`. There is also a class in the jgrade package (in the [tests](https://github.com/tkutche1/jgrade/tree/development/src/test/java/edu/jhu/cs/jgrade) directory of this repository named `AllJGradeTests` - this class is a suite of all unit tests for the program. Note that to test the `GradedTestListener` class there are some dummy unit test inner classes that are not actual tests (many of which fail intentionally).

### Requirements
JGrade is written in [Java 8](https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html). Since the library has classes designed to run alongside JUnit, [JUnit 4](https://junit.org/junit4/) is a dependency for the entire project (as opposed to just running the projects own unit tests). The [org.json](https://mvnrepository.com/artifact/org.json/json) package is used in producing correctly formatted JSON output, and the [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/) library is used for reading the command line in the main program.

### Full API
The full API (generated from the Javadoc comments) can be retrieved from downloading the zip in the [api folder](https://github.com/tkutche1/jgrade/tree/development/docs/api) and opening the index.html in the top level. 
