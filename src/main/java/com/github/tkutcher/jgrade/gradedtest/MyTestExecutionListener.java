
package com.github.tkutcher.jgrade.gradedtest;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.tkutcher.jgrade.gradedtest.GradedTest;
import com.github.tkutcher.jgrade.gradedtest.GradedTestResult;

import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryListener;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.junit.platform.launcher.PostDiscoveryFilter;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.reporting.legacy.xml.LegacyXmlReportGeneratingListener;

public class MyTestExecutionListener implements TestExecutionListener {

    private List<GradedTestResult> gradedTestResults;
    private int numFailedGradedTests;
    private ByteArrayOutputStream testOutput;
    private PrintStream originalOutStream;
    private GradedTestResult currentGradedTestResult;

    /**
     * Constructor for a new listener. Initializes a list of
     * {@link GradedTestResult}s and remembers the original
     * <code>System.out</code> to restore it.
     */
    public MyTestExecutionListener() {
        this.gradedTestResults = new ArrayList<>();
        this.numFailedGradedTests = 0;
        this.testOutput = new ByteArrayOutputStream();
        this.originalOutStream = System.out;
    }

    /**
     * Get the count of graded tests for this listener.
     * 
     * @return The number of graded tests.
     */
    public int getNumGradedTests() {
        return this.gradedTestResults.size();
    }

    /**
     * Get the list of {@link GradedTestResult}.
     * 
     * @return The list of {@link GradedTestResult}.
     */
    public List<GradedTestResult> getGradedTestResults() {
        return this.gradedTestResults;
    }

    /**
     * Get the number of failed graded tests.
     * 
     * @return The number of graded tests that failed.
     */
    public int getNumFailedGradedTests() {
        return numFailedGradedTests;
    }

    /**
     * Called when the execution of the {@link TestPlan} has started,
     * <em>before</em> any test has been executed.
     *
     * @param testPlan describes the tree of tests about to be executed
     */
    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {

    }

    /**
     * Called when the execution of the {@link TestPlan} has finished,
     * <em>after</em> all tests have been executed.
     *
     * @param testPlan describes the tree of tests that have been executed
     */
    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
    }

    /**
     * Called when a new, dynamic {@link TestIdentifier} has been registered.
     *
     * <p>
     * A <em>dynamic test</em> is a test that is not known a-priori and
     * therefore not contained in the original {@link TestPlan}.
     *
     * @param testIdentifier the identifier of the newly registered test
     *                       or container
     */
    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
        System.out.println("dynamicTestRegistered");
    }

    /**
     * Called when the execution of a leaf or subtree of the {@link TestPlan}
     * has been skipped.
     *
     * <p>
     * The {@link TestIdentifier} may represent a test or a container. In
     * the case of a container, no listener methods will be called for any of
     * its descendants.
     *
     * <p>
     * A skipped test or subtree of tests will never be reported as
     * {@linkplain #executionStarted started} or
     * {@linkplain #executionFinished finished}.
     *
     * @param testIdentifier the identifier of the skipped test or container
     * @param reason         a human-readable message describing why the execution
     *                       has been skipped
     */
    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        System.out.println("executionSkipped");

    }

    /** 
     * is this a test, and is the method annotated with GradescopeTest
     * 
     */

    public MethodSource getMethodSource(TestIdentifier testIdentifier) {

        if (!testIdentifier.isTest()) return null;

        Optional<TestSource> ots = testIdentifier.getSource();
        if (!ots.isPresent()) return null;

        TestSource ts = ots.get();        

        try {
            MethodSource ms = (MethodSource) ts;
            return ms;
        } catch (ClassCastException cce) {
            return null;
        }
    }

     /** 
     * is this a test, and is the method annotated with GradescopeTest
     * 
     */

    public GradedTest getGradedTest(MethodSource ms) {

        if (ms==null) return null;

        try {
            Method m = ms.getJavaMethod();
            GradedTest gradedTest = m.getAnnotation(GradedTest.class);
            return gradedTest;
        } catch (ClassCastException cce) {
            return null;
        }
    }


    /**
     * Called when the execution of a leaf or subtree of the {@link TestPlan}
     * is about to be started.
     *
     * <p>
     * The {@link TestIdentifier} may represent a test or a container.
     *
     * <p>
     * This method will only be called if the test or container has not
     * been {@linkplain #executionSkipped skipped}.
     *
     * <p>
     * This method will be called for a container {@code TestIdentifier}
     * <em>before</em> {@linkplain #executionStarted starting} or
     * {@linkplain #executionSkipped skipping} any of its children.
     *
     * @param testIdentifier the identifier of the started test or container
     */
    @Override
    public void executionStarted(TestIdentifier testIdentifier) {

        MethodSource ms = getMethodSource(testIdentifier);
        GradedTest gradedTest = getGradedTest(ms);

        this.currentGradedTestResult = null;

        if (gradedTest != null) {
            this.currentGradedTestResult =  new GradedTestResult(
                gradedTest.name(),
                gradedTest.number(),
                gradedTest.points(),
                gradedTest.visibility()
            );

            this.currentGradedTestResult.setScore(gradedTest.points());
        }

        this.testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.testOutput));
    }

    public void printAll(
            MethodSource methodSource,
            TestExecutionResult testExecutionResult,
            GradedTest gradedTest) {
        System.out.println("ProcessForGradescope:");
        System.out.println(" class=" + methodSource.getClassName());
        System.out.println(" method=" + methodSource.getMethodName());
        System.out.println(" testExecutionResult=" + testExecutionResult);
        System.out.println(" gradedTest=" + gradedTest);
    }

    /**
     * Called when the execution of a leaf or subtree of the {@link TestPlan}
     * has finished, regardless of the outcome.
     *
     * <p>
     * The {@link TestIdentifier} may represent a test or a container.
     *
     * <p>
     * This method will only be called if the test or container has not
     * been {@linkplain #executionSkipped skipped}.
     *
     * <p>
     * This method will be called for a container {@code TestIdentifier}
     * <em>after</em> all of its children have been
     * {@linkplain #executionSkipped skipped} or have
     * {@linkplain #executionFinished finished}.
     *
     * <p>
     * The {@link TestExecutionResult} describes the result of the execution
     * for the supplied {@code TestIdentifier}. The result does not include or
     * aggregate the results of its children. For example, a container with a
     * failing test will be reported as {@link Status#SUCCESSFUL SUCCESSFUL} even
     * if one or more of its children are reported as {@link Status#FAILED FAILED}.
     *
     * @param testIdentifier      the identifier of the finished test or container
     * @param testExecutionResult the (unaggregated) result of the execution for
     *                            the supplied {@code TestIdentifier}
     *
     * @see TestExecutionResult
     */
    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {

        MethodSource ms = getMethodSource(testIdentifier);
        GradedTest gradedTest = getGradedTest(ms);

        if (gradedTest != null) {
            // printAll(ms, testExecutionResult, gradedTest);
        }

        if (this.currentGradedTestResult != null) {
            this.currentGradedTestResult.addOutput(testOutput.toString());
            this.gradedTestResults.add(this.currentGradedTestResult);
        }

        this.currentGradedTestResult = null;
        System.setOut(originalOutStream);
    }

    /**
     * Called when additional test reporting data has been published for
     * the supplied {@link TestIdentifier}.
     *
     * <p>
     * Can be called at any time during the execution of a test plan.
     *
     * @param testIdentifier describes the test or container to which the entry
     *                       pertains
     * @param entry          the published {@code ReportEntry}
     */
    @Override
    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
        // System.out.println("reportingEntryPublished");
    }

}
