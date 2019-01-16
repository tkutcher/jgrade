package edu.jhu.cs226.instructor.tests;

import org.junit.Before;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class MainProgramUnitTester {

    public static final String SOLUTION_PACKAGE = "instructor.solutions";
    public static final String STUDENT_PACKAGE = "student";

    private static final String BASE_PACKAGE = "edu.jhu.cs226";
    private static final String CLASS_LOCATION = "classes/:*";

    protected boolean PRINT_OUTPUT = false;

    protected abstract String getMainProgramName();

    protected abstract String getPackage();

    protected List<String> command; // Subclassing classes can append to this to add arguments
    protected ProcessBuilder builder; // Subclassing classes can edit things from this like redirectErrorStream

    @Before
    public void initCommand() {
        String mainProgram = String.format("%s.%s.%s", BASE_PACKAGE, getPackage(), getMainProgramName());
        this.command = new ArrayList<>(Arrays.asList("java", "-cp", CLASS_LOCATION, mainProgram));
        this.builder = new ProcessBuilder();
    }

    protected ExecutionResult runCommand(String withInput) {
        this.builder.command(this.command);
        ExecutionResult output = executeProcess(this.builder, withInput);
        if (PRINT_OUTPUT)
            output.dump();
        return output;
    }

    protected ExecutionResult runCommand() {
        return runCommand(null);
    }


    private static String getStringFromStream(InputStream stream) throws IOException {
        byte streamBytes[] = new byte[stream.available()];
        stream.read(streamBytes, 0, streamBytes.length);
        return new String(streamBytes);
    }

    public static ExecutionResult executeProcess(ProcessBuilder builder, String toWriteIn) {
        try {
            Process proc = builder.start();
            OutputStream driverStdin = proc.getOutputStream();
            InputStream driverStdout = proc.getInputStream();
            InputStream driverStderr = proc.getErrorStream();

            // FIXME - Is there a fancier way to do this?
            if (toWriteIn != null) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(driverStdin));
                writer.write(toWriteIn);
                writer.flush();
                writer.close();
            }

            proc.waitFor();

            return new ExecutionResult(getStringFromStream(driverStdout), getStringFromStream(driverStderr));

        } catch (IOException | InterruptedException e) {
            throw new InternalError(e);
        }
    }

    public static ExecutionResult executeProcess(ProcessBuilder builder) {
        return executeProcess(builder, null);
    }
}
