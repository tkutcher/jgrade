package com.github.tkutche1.jgrade;

import org.junit.Before;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A class to assist in testing the main method of programs that are designed
 * with a command line interface (as many introductory assignments are). The
 * public static methods can be used to assist in getting output, or a class
 * can extend this class, set the invocation, and build unit tests around it
 * to test functionality via JUnit.
 */
public abstract class CLITester {

    static class ExecutionResult implements CLIResult {
        private String stdOutOutput;
        private String stdErrOutput;
        private int exitValue;

        ExecutionResult(String stdOutOutput, String stdErrOutput, int exitValue) {
            this.stdOutOutput = stdOutOutput;
            this.stdErrOutput = stdErrOutput;
            this.exitValue = exitValue;
        }

        @Override
        public String getOutput(STREAM stream) {
            return stream == STREAM.STDOUT ? this.stdOutOutput : this.stdErrOutput;
        }

        @Override
        public String getOutput() {
            return getOutput(STREAM.STDOUT);
        }

        @Override
        public List<String> getOutputByLine(STREAM stream) {
            return splitByLines(getOutput(stream));
        }

        @Override
        public int exitValue() {
            return this.exitValue;
        }


        void dump() {
            System.out.println(stdOutOutput);
            if (stdErrOutput.length() > 0) {
                System.out.println("---\nSTD_ERR:");
                System.out.println(stdErrOutput);
            }
        }

        private static List<String> splitByLines(String s) {
            return s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("[\\r\\n]+"));
        }
    }


    private List<String> command; // Subclassing classes can append to this to add arguments
    private ProcessBuilder builder; // Subclassing classes can edit things from this like redirectErrorStream
    private boolean printOutput; // If true, print the result's output

    /**
     * Get the invocation for the CLI program. {@link #initCommand()} (which
     * is annotated with @Before) calls this method to set the command.
     * Subclasses implement this method to tell it how to setup the command.
     * For example, if a user had main program <code>Hello</code>, they may
     * do the following to implement this method:
     * <code>
     *     return Arrays.asList(new String[] {"java", "-cp", "lib/:.", "Hello"});
     * </code>
     * @return The List of Strings that would invoke the program.
     */
    protected abstract List<String> getInvocation();

    /**
     * Initializes the command for the test (and is annotated with
     * {@link Before}. Calls the abstract {@link #getInvocation()} and
     * initializes a new {@link ProcessBuilder}.
     */
    @Before
    public void initCommand() {
        this.command = getInvocation();
        this.builder = new ProcessBuilder();
    }

    /**
     * Get the command that was initialized from {@link #initCommand()}.
     * If adding command line arguments, call this method and append to the
     * command the arguments. For example, in a specific method the user
     * could do <code> getCommand().append("arg1"); </code> before calling
     * {@link #runCommand()}.
     * @return The current command (which is a List of Strings).
     */
    public List<String> getCommand() { return this.command; }

    /**
     * Get the {@link ProcessBuilder} that was initialized from
     * {@link #initCommand()}.
     * If wanting to edit things about the builder for instance
     * {@link ProcessBuilder#redirectErrorStream()} use this method to access
     * the builder.
     * @return The {@link ProcessBuilder} being used.
     */
    public ProcessBuilder getBuilder() { return this.builder; }

    /**
     * Set whether or not to print the captured output. If true, then when
     * {@link #runCommand()} is called it will automatically print the output
     * to System.out.
     * @param to The boolean value to set whether or not to print output.
     */
    public void setPrintOutput(boolean to) { this.printOutput = to; }

    /**
     * Run a command with input. See {@link #runCommand()}.
     * @param withInput The input to input to the execution.
     * @return The result of the execution of the program.
     */
    protected CLIResult runCommand(String withInput) {
        this.builder.command(this.command);
        CLIResult output = executeProcess(this.builder, withInput);
        if (printOutput)
            ((ExecutionResult) output).dump();
        return output;
    }

    /**
     * Run the command as it is currently set for this class without feeding
     * in anything to stdin. If the program takes command line input, see
     * {@link #runCommand(String)}.
     * @return The result of the execution of the program.
     */
    protected CLIResult runCommand() {
        return runCommand(null);
    }

    /**
     * Execute a process provided a {@link ProcessBuilder} that has a command
     * set to invoke the process, and a String for input to feed to the
     * program.
     * @param builder The {@link ProcessBuilder} to use for the command.
     * @param toWriteIn Input for the program that it reads from stdin.
     * @return The {@link CLIResult} containing the output from the run.
     */
    public static CLIResult executeProcess(ProcessBuilder builder,
                                           String toWriteIn) {
        try {
            Process proc = builder.start();
            OutputStream driverStdin = proc.getOutputStream();
            InputStream driverStdout = proc.getInputStream();
            InputStream driverStderr = proc.getErrorStream();

            // FIXME - Is there a fancier way to do this?
            if (toWriteIn != null) {
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(driverStdin));
                writer.write(toWriteIn);
                writer.flush();
                writer.close();
            }

            int exitValue = proc.waitFor();

            return new ExecutionResult(getStringFromStream(driverStdout),
                    getStringFromStream(driverStderr), exitValue);

        } catch (IOException | InterruptedException e) {
            throw new InternalError(e);
        }
    }

    /**
     * Executes a process with a provided {@link ProcessBuilder} but sends
     * no input to the program. Other than that, same as
     * {@link #executeProcess(ProcessBuilder, String)}.
     * @param builder The {@link ProcessBuilder} to use to run.
     * @return The {@link CLIResult} containing the output from the run.
     */
    public static CLIResult executeProcess(ProcessBuilder builder) {
        return executeProcess(builder, null);
    }

    // Reads from an InputStream and returns the String.
    private static String getStringFromStream(InputStream stream) throws IOException {
        byte streamBytes[] = new byte[stream.available()];
        stream.read(streamBytes, 0, streamBytes.length);
        return new String(streamBytes);
    }
}
