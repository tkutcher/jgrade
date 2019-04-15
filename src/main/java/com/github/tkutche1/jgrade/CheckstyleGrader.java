package com.github.tkutche1.jgrade;

import com.github.tkutche1.jgrade.gradedtest.GradedTestResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.github.tkutche1.jgrade.gradedtest.GradedTestResult.VISIBLE;

/**
 * Class to assist in getting a {@link GradedTestResult} for checkstyle. It
 * sets a max score, and a points per deduction, then deducts up to the max
 * score number of points. Assumes is checking an entire directory but
 * excludes any files with "test" in the name by default (since we don't
 * really require JUnit files be checkstyle compliant). To configure it for
 * specific files the client has to override {@link #isFileToCheck(Path)}.
 * @version 1.0.0
 */
public class CheckstyleGrader {

    private static final String CHECKSTYLE_NAME = "Checkstyle";
    private static final String CHECKSTYLE_FORMAT = "xml";
    private static final String FILE_TAG = "file";
    private static final String FILE_NAME_ATTR = "name";
    private static final String ERROR_TAG = "error";
    private static final String LINE_ATTR = "line";
    private static final String COL_ATTR = "column";
    private static final String MSG_ATTR = "message";
    private static final String SRC_ATTR = "source";

    private double points;
    private double deduct;
    private String pathToJar;
    private String dirToCheck;
    private String config;

    private Map<String, Integer> errorTypes;

    /**
     * Instantiate a new CheckstyleGrader.
     * @param points The total number of points for the checkstyle test.
     * @param deduct The number of points to deduct per error.
     * @param pathToJar The path to the checkstyle jar executable.
     * @param dirToCheck The directory of files to check.
     */
    public CheckstyleGrader(double points, double deduct,
                            String pathToJar, String dirToCheck) {
        this.points = points;
        this.deduct = deduct;
        this.pathToJar = pathToJar;
        this.dirToCheck = dirToCheck;
        this.config = null;
        this.errorTypes = new TreeMap<>();
    }

    /**
     * Set a configuration file to use for the checkstyle run.
     * @param config - The file to use as the -c argument to checkstyle.
     */
    public void setConfig(String config) {
        this.config = config;
    }

    /**
     * Run the graded for a {@link GradedTestResult}. This will run the jar
     * for xml output and parse that output. If a configuration file has been
     * specified from {@link #setConfig(String)} then it will add the config
     * to the command. Will include all files that {@link #isFileToCheck(Path)}
     * returns true for, which by default is any java file not containing
     * "test" in it's name. Will deduct to 0 points for each error.
     * @return The generated result.
     */
    public GradedTestResult runForGradedTestResult() {
        List<String> command = new ArrayList<>(Arrays.asList("java", "-jar",
                this.pathToJar, "-f", CHECKSTYLE_FORMAT));
        if (this.config != null) {
            command.add("-c");
            command.add(this.config);
        }

        try {
            Files.walk(Paths.get(dirToCheck))
                    .filter(CheckstyleGrader::isFileToCheck)
                    .forEach(path -> command.add(path.toString()));
            String xmlOutput = CLITester.executeProcess(
                    new ProcessBuilder(command))
                    .getOutput(CLIResult.STREAM.STDOUT);
            return xmlToGradedTestResult(xmlOutput);
        } catch (InternalError | IOException e) {
            e.printStackTrace();
            e.printStackTrace(System.err);
            return internalErrorResult(e.toString());
        }
    }

    /**
     * Get the map of error types to their count.
     * @return The map of error types to their count.
     */
    public Map<String, Integer> getErrorTypes() {
        return this.errorTypes;
    }

    /**
     * Get the number of different error types encountered.
     * @return The number of different error types encountered.
     */
    public int getErrorTypeCount() {
        return this.errorTypes.size();
    }

    // FIXME - Alternative to make this take some interface that calls the
    //   static boolean function.
    /**
     * Boolean function for whether or not a file should be included in
     * checkstyle's run. By default it only includes files that are
     * java files and excludes any containing "test" in the directory.
     * If a client wanted to include more or exclude others they would
     * have to subclass and override this.
     * @param path The file to consider.
     * @return True if it should be checked.
     */
    protected static boolean isFileToCheck(Path path) {
        String s = path.toString();
        return s.endsWith(".java") && !s.toLowerCase().contains("test");
    }

    private GradedTestResult initResult() {
        return new GradedTestResult(CHECKSTYLE_NAME, "", this.points, VISIBLE);
    }

    private GradedTestResult internalErrorResult(String msg) {
        GradedTestResult result = initResult();
        result.addOutput("Internal Error!\n");
        result.addOutput(msg);
        return result;
    }

    private GradedTestResult xmlToGradedTestResult(String checkstyleOutput) throws InternalError {
        String xml = stripNonXml(checkstyleOutput);
        Document d = getXmlAsDocument(xml);
        GradedTestResult result = initResult();
        NodeList filesWithErrors = d.getElementsByTagName(FILE_TAG);

        int numErrors = 0;
        for (int i = 0; i < filesWithErrors.getLength(); i++) {
            numErrors += addOutputForFileNode(result, filesWithErrors.item(i));
        }

        result.setScore(Math.max(this.points - (numErrors * this.deduct), 0));

        if (numErrors == 0) {
            result.addOutput("Passed all checks!");
        }

        return result;
    }

    private static String stripNonXml(String s) {
        return s.substring(s.indexOf('<'), s.lastIndexOf('>') + 1);
    }

    private static Document getXmlAsDocument(String xml) throws InternalError {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            return builder.parse(is);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new InternalError(e);
        }
    }

    private static String getAttributeValue(String prefix, Node attribute) {
        return attribute == null ? "" : String.format("%s%s", prefix, attribute.getNodeValue());
    }

    private static String getAttributeValue(Node attribute) {
        return getAttributeValue("", attribute);
    }

    private String getOutputForErrorNode(NamedNodeMap attributes) {
        if (attributes == null) {
            throw new InternalError();
        }
        Node lineAttribute = attributes.getNamedItem(LINE_ATTR);
        Node columnAttribute = attributes.getNamedItem(COL_ATTR);
        Node messageAttribute = attributes.getNamedItem(MSG_ATTR);
        String errorTypeAttribute = getAttributeValue(attributes.getNamedItem(SRC_ATTR));
        if (errorTypeAttribute.contains(".")) {
            String[] split = errorTypeAttribute.split("\\.");
            errorTypeAttribute = split[split.length - 1];
            if (!this.errorTypes.containsKey(errorTypeAttribute)) {
                this.errorTypes.put(errorTypeAttribute, 1);
            } else {
                this.errorTypes.put(errorTypeAttribute, this.errorTypes.get(errorTypeAttribute) + 1);
            }
        }

        return String.format("\t%-20s - %s [%s]\n",
                getAttributeValue("line: ", lineAttribute)
                + getAttributeValue(", column", columnAttribute),
                getAttributeValue(messageAttribute),
                errorTypeAttribute);
    }

    private int addOutputForFileNode(GradedTestResult result, Node elementNode) {
        String fullPath = elementNode.getAttributes().getNamedItem(FILE_NAME_ATTR).toString();
        String fileName = fullPath.substring(fullPath.lastIndexOf('/') + 1, fullPath.length() - 1);
        NodeList errorNodes = ((Element) elementNode).getElementsByTagName(ERROR_TAG);

        if (errorNodes.getLength() > 0) {
            result.addOutput(fileName + ":\n");
        }

        for (int i = 0; i < errorNodes.getLength(); i++) {
            result.addOutput(getOutputForErrorNode(errorNodes.item(i).getAttributes()));
        }

        return errorNodes.getLength();
    }
}
