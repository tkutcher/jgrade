package com.github.tkutche1.jgrade;

import com.github.tkutche1.jgrade.gradedtest.AllGraderTests;
import com.github.tkutche1.jgrade.gradescope.GradescopeJsonObserverTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GraderTest.class,
        JGradeCommandLineTest.class,
        AllGraderTests.class,
        GradescopeJsonObserverTest.class,
        CLITesterExecutionResultTest.class,
})
public class AllJGradeTests { }
