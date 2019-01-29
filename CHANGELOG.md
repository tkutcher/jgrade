# CHANGELOG

- [1.0.0-alpha](#1.0.0\-alpha)
- [1.0.0](#1.0.0)

---

### 1.0.0-alpha
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
- Some (very basic) unit tests in `CLITesterExecutionResultTest` to test these tweaks and that they work as expected.
- `getOutput()` without parameters defaults to returning the standard out.

