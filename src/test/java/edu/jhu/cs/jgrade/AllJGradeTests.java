package edu.jhu.cs.jgrade;

import edu.jhu.cs.jgrade.gradedtest.AllGraderTests;
import edu.jhu.cs.jgrade.gradescope.GradescopeJsonObserverTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GraderTest.class,
        JGradeCommandLineTest.class,
        AllGraderTests.class,
        GradescopeJsonObserverTest.class,
})
public class AllJGradeTests { }
