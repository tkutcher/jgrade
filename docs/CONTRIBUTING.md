# CONTRIBUTING

_A guide for contributing to this repository ._

- [SOP](#sop)
  - [Issues](#issues)
  - [Branching](#branching)
  - [Pull Requests](#pull-requests)
  - [Testing](#testing)
- [Style](#style)

---

## SOP

### Issues
- Any feature, bug, etc., worked on should have a corresponding GitHub issue.
- If the issue does not exist, create it.
  - Add the corresponding labels (bug, enhancement, etc.).
  - Add a relevant description along with the title.
- If you are working on a feature, leave a comment on the issue

### Branching
- Any feature to implement should branch off the `development` branch.
- The `master` branch is always a _clean_ release.
- Ideally, the branch name should begin with the number of the issue you are working on.
- All lower-case, hyphen separated (e.g. `12-some-example` would be a branch that solves issue #12 which is about "some example").

### Testing
- To merge any new feature to development, there should be a relevant unit test setup in the `test` folder, in the same package structure as the class they are testing.
- Extensive tests are not necessary, but a skeleton should be there.
- You should add the class to the `All...` `@Suite` class in the relevant package. 
  
### Pull Requests
- After the work has been implemented, create a pull request for your branch in to `development` describing the changes (no template has been set up yet).
- If tests pass, it will be reviewed.


## Style

### Java Style
No checkstyle config has been set up _yet_. Stay tuned. Not particularly picky.

### Branching Names
See the note in the [SOP branching section](#branching)