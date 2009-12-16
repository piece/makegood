package com.piece_framework.makegood.launch.phpunit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestResultConverter {
    public static List<TestSuite> convert(File result) throws FileNotFoundException {
        if (!result.exists()) {
            throw new FileNotFoundException();
        }
        List<TestSuite> testSuites = new ArrayList<TestSuite>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(result);

            Element root = document.getDocumentElement();
            NodeList nodes = root.getChildNodes();
            for (int i = 0, count = nodes.getLength(); i < count; ++i) {
                TestSuite suite = (TestSuite) convertTestResult(nodes.item(i));
                if (suite != null) {
                    testSuites.add(suite);
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return testSuites;
    }

    private static TestResult convertTestResult(Node node) {
        if (!node.hasAttributes()) {
            return null;
        }

        if (isTestSuiteNode(node)) {
            return convertTestSuite(node);
        } else if (isTestCaseNode(node)) {
            return convertTestCase(node);
        }
        return null;
    }

    private static TestResult convertTestSuite(Node node) {
        TestSuite suite = new TestSuite(createAttributesMap(node));

        NodeList childNodes = node.getChildNodes();
        for (int i = 0, count = childNodes.getLength(); i < count; ++i) {
            suite.addTestResult(convertTestResult(childNodes.item(i)));
        }

        return suite;
    }

    private static TestResult convertTestCase(Node node) {
        TestCase testCase = new TestCase(createAttributesMap(node));

        NodeList childNodes = node.getChildNodes();
        for (int i = 0, count = childNodes.getLength(); i < count; ++i) {
            if (isFailureNode(childNodes.item(i))
                || isErrorNode(childNodes.item(i))
                ) {
                testCase.problem = convertProblem(childNodes.item(i));
                break;
            }
        }

        return testCase;
    }

    private static Problem convertProblem(Node node) {
        Problem problem = null;
        if (isFailureNode(node)) {
            problem = new Problem(ProblemType.Failure);
        } else if (isErrorNode(node)) {
            problem = new Problem(ProblemType.Error);
        } else {
            return null;
        }
        if (node.getAttributes().getNamedItem("type") != null) {    //$NON-NLS-1$
            problem.typeClass = node.getAttributes().getNamedItem("type").getNodeValue(); //$NON-NLS-1$
        }
        problem.content = node.getTextContent();
        return problem;
    }

    private static Map<String, String> createAttributesMap(Node node) {
        Map<String, String> attributes = new HashMap<String, String>();
        for (int i = 0, count = node.getAttributes().getLength(); i < count; ++i) {
            attributes.put(node.getAttributes().item(i).getNodeName(),
                           node.getAttributes().item(i).getNodeValue()
                           );
        }
        return attributes;
    }

    private static boolean isTestSuiteNode(Node node) {
        return node.getNodeName().equals("testsuite"); //$NON-NLS-1$
    }

    private static boolean isTestCaseNode(Node node) {
        return node.getNodeName().equals("testcase"); //$NON-NLS-1$
    }

    private static boolean isFailureNode(Node node) {
        return node.getNodeName().equals("failure"); //$NON-NLS-1$
    }

    private static boolean isErrorNode(Node node) {
        return node.getNodeName().equals("error"); //$NON-NLS-1$
    }
}
