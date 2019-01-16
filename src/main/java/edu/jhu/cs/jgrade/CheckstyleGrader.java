package edu.jhu.cs226.instructor.autograder;

import edu.jhu.cs226.instructor.autograder.base.GradedTestResult;
import edu.jhu.cs226.instructor.tests.MainProgramUnitTester;
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

import static edu.jhu.cs226.instructor.autograder.base.Consts.GradescopeJson.VISIBLE;

class CheckstyleGrader {

    private static final String CHECKSTYLE_NAME = "Checkstyle";
    private static final double CHECKSTYLE_POINTS = 10.0;
    private static final String CHECKSTYLE_JAR = "lib/checkstyle-8.12-all.jar";
    private static final String CHECKSTYLE_CONFIG = "res/cs226_checks.xml";
    private static final String CHECKSTYLE_FORMAT = "xml";
    private static final String BASE_PATH = "src/student/java/edu/jhu/cs226/student/";

    private static final String FILE_TAG = "file";
    private static final String FILE_NAME_ATTR = "name";
    private static final String ERROR_TAG = "error";
    private static final String LINE_ATTR = "line";
    private static final String COL_ATTR = "column";
    private static final String MSG_ATTR = "message";

    private static GradedTestResult initResult() {
        return new GradedTestResult(CHECKSTYLE_NAME, "", CHECKSTYLE_POINTS, VISIBLE);
    }

    private static GradedTestResult internalErrorResult(String msg) {
        GradedTestResult result = initResult();
        result.addOutput("Internal Error!\n");
        result.addOutput(msg);
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

    private static GradedTestResult xmlToGradedTestResult(String checkstyleOutput) throws InternalError {
        String xml = stripNonXml(checkstyleOutput);
        Document d = getXmlAsDocument(xml);
        GradedTestResult result = initResult();
        NodeList filesWithErrors = d.getElementsByTagName(FILE_TAG);
        int numErrors = 0;
        for (int i = 0; i < filesWithErrors.getLength(); i++)
            numErrors += addOutputForFileNode(result, filesWithErrors.item(i));
        result.setScore(Math.max(CHECKSTYLE_POINTS - numErrors, 0));
        return result;
    }

    private static String getAttributeValue(String prefix, Node attribute) {
        return attribute == null ? "" : String.format("%s%s", prefix, attribute.getNodeValue());
    }

    private static String getAttributeValue(Node attribute) {
        return getAttributeValue("", attribute);
    }

    private static String getOutputForErrorNode(NamedNodeMap attributes) {
        if (attributes == null)
            throw new InternalError();
        Node lineAttribute = attributes.getNamedItem(LINE_ATTR);
        Node columnAttribute = attributes.getNamedItem(COL_ATTR);
        Node messageAttribute = attributes.getNamedItem(MSG_ATTR);
        return String.format("\t%-20s - %s\n", getAttributeValue("line: ", lineAttribute)
                + getAttributeValue(", column", columnAttribute), getAttributeValue(messageAttribute));
    }

    private static int addOutputForFileNode(GradedTestResult result, Node elementNode) {
        String fullPath = elementNode.getAttributes().getNamedItem(FILE_NAME_ATTR).toString();
        String fileName = fullPath.substring(fullPath.lastIndexOf('/') + 1, fullPath.length() - 1);
        NodeList errorNodes = ((Element) elementNode).getElementsByTagName(ERROR_TAG);
        if (errorNodes.getLength() > 0)
            result.addOutput(fileName + ":\n");
        for (int i = 0; i < errorNodes.getLength(); i++)
            result.addOutput(getOutputForErrorNode(errorNodes.item(i).getAttributes()));
        return errorNodes.getLength();
    }

    private static boolean isFileToCheck(Path path) {
        String s = path.toString();
        return s.endsWith(".java") && !s.contains("test") && !s.contains("bench");
    }

    public static GradedTestResult runForGradedTestResult() {
        List<String> command = new ArrayList<>(Arrays.asList("java", "-jar", CHECKSTYLE_JAR,
                "-c", CHECKSTYLE_CONFIG, "-f", CHECKSTYLE_FORMAT));

        try {
            Files.walk(Paths.get(BASE_PATH))
                    .filter(CheckstyleGrader::isFileToCheck)
                    .forEach(path -> command.add(path.toString()));
            String xmlOutput = MainProgramUnitTester.executeProcess(new ProcessBuilder(command)).getStdOutOutput();
            return xmlToGradedTestResult(xmlOutput);
        } catch (InternalError | IOException | RuntimeException e) {
            return internalErrorResult(e.toString());
        }
    }
}
