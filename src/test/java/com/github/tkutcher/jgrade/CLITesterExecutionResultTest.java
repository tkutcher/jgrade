package com.github.tkutcher.jgrade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void defaultGetStd() {
        CLIResult unit = new CLITester.ExecutionResult("stdout", "stderr", 0);
        assertEquals("stdout", unit.getOutput());
    }
}
