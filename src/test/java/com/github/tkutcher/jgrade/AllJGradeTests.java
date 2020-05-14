package com.github.tkutcher.jgrade;

import com.github.tkutcher.jgrade.gradedtest.AllGraderTests;
import com.github.tkutcher.jgrade.gradescope.GradescopeJsonFormatterTest;
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
