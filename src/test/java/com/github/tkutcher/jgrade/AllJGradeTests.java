package com.github.tkutcher.jgrade;

import com.github.tkutcher.jgrade.gradedtest.AllGraderTests;
import com.github.tkutcher.jgrade.gradescope.GradescopeJsonObserverTest;
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
