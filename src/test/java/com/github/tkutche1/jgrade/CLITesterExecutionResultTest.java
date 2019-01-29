package com.github.tkutche1.jgrade;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CLITesterExecutionResultTest {

    @Test
    public void getStdout() {
        CLIResult unit = new CLITester.ExecutionResult("test1", "", 1);
        assertEquals("test1", unit.getOutput(CLIResult.STREAM.STDOUT));
    }

    @Test
    public void getByLineOnEmptyString() {
        CLIResult unit = new CLITester.ExecutionResult("test1", "", 1);
        assertEquals(0, unit.getOutputByLine(CLIResult.STREAM.STDERR).size());
    }

    @Test
    public void getExitValue() {
        CLIResult unit = new CLITester.ExecutionResult("test1", "", 1);
        assertEquals(1, unit.exitValue());
    }
}
