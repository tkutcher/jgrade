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
