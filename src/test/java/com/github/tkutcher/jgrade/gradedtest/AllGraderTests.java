package com.github.tkutcher.jgrade.gradedtest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GradedTestListenerTest.class,
        GradedTestResultTest.class,
})
public class AllGraderTests { }
