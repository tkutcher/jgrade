# CHANGELOG

Go To:

**[Most Recent (1.1.0)](#1.1.0)**

- [1.0.0-alpha](#1.0.0-(alpha))
- [1.0.0](#1.0.0)
  - [1.0.1](#1.0.1)
  - [1.0.2](#1.0.2)
  - [1.0.3](#1.0.3)
- [1.1.0](#1.1.0)

---

### 1.0.0 (alpha)
- For internal use
- Maven setup
- Main program that takes a class name as a String on the command line and runs the annotated methods in that class to produce output
  - `@AfterGrading`, `@BeforeGrading`, and `@Grade` annotations.
  - JSON output
- Classes for producing output specific to Gradescope's specifications.
  - _Note: **no** textual output support._


### 1.0.0
_1.17.2019_
- Initial release
- Added `CLITester` and `CLIResult` for command line help.
- Added `CheckstyleGrader` for creating a `GradedTestResult` based off a checkstyle run.
- Made domain for package `com.github.tkutche1`
- pom to correctly build jar with dependencies (appended with `-all`).
- pom with option to build javadoc.
- Full gradescope example.

#### 1.0.1
_1.24.2019_
- Bug in `CheckstyleGrader` excluding files named with `test` anywhere in the path, now ignores case.

#### 1.0.2
_1.28.2019_
- Bug in `CLIResult` that returned a `List` of size `1` rather than `0` when the stream actually had an empty string.
- Captures the exit value for the sub-process that runs the main program being tested.
- `getOutput()` without parameters defaults to returning the standard out.
- Some (very basic) unit tests in `CLITesterExecutionResultTest` to test these tweaks and that they work as expected.

#### 1.0.3
_1.29.2019_
- Checkstyle compliance in all files.
- Docs based on GitHub's community standards.
- Maven checkstyle plugin added to object model.
- Updated naming of jars to **not** include patch number.


### 1.1.0
_1.31.2019_
- Added strategy design pattern to `Grader` so when a client calls `runJUnitGradedTests` the strategy for those test results treats them accordingly.
  - This is the `GraderStrategy` interface class
  - Added a private class `DefaultGraderStrategy` that does nothing so as to preserve original behavior for tests, etc.
  - _Note this pattern should probably eventually be moved out to be a strategy for the `GradedTestListener` class rather than the `Grader` but would require a lot more work and needed this for this semester._
- Added `DeductiveGraderStrategy` class to go through graded test results from JUnit tests and treat their scores as deductive.
  - Constructor sets a starting score, failed tests deduct by the amount of points they are worth up to some floor (0 by default).
  - Tracks the total points deducted.
  - Appends a message to the test result's output if not all (or no) points were deducted so as to not go beyond the floor.
- `DeductiveGraderStrategyTest` unit tests
  - Added to `AllJGradeTests`
