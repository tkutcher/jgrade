package com.github.tkutche1.jgrade;

import com.github.tkutche1.jgrade.gradedtest.AllGraderTests;
import com.github.tkutche1.jgrade.gradescope.GradescopeJsonFormatterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GraderTest.class,
        JGradeCommandLineTest.class,
        AllGraderTests.class,
        GradescopeJsonFormatterTest.class,
        CLITesterExecutionResultTest.class,
        DeductiveGraderStrategyTest.class,
})
public class AllJGradeTests { }
