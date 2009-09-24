package com.piece_framework.makegood.launch.phpunit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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
                TestSuite suite = (TestSuite) scanNode(nodes.item(i));
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

    private static TestResult scanNode(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return null;
        }

        if (node.getNodeName().equals("testsuite")) {
            TestSuite suite = new TestSuite();
            suite.name = attributes.getNamedItem("name").getNodeValue();
            if (attributes.getNamedItem("file") != null) {
                suite.file = attributes.getNamedItem("file").getNodeValue();
            }
            if (attributes.getNamedItem("fullPackage") != null) {
                suite.fullPackage = attributes.getNamedItem("fullPackage").getNodeValue();
            }
            if (attributes.getNamedItem("package") != null) {
                suite.packageName = attributes.getNamedItem("package").getNodeValue();
            }
            if (attributes.getNamedItem("tests") != null) {
                suite.testCount = Integer.parseInt(attributes.getNamedItem("tests").getNodeValue());
            }
            if (attributes.getNamedItem("assertions") != null) {
                suite.assertionCount = Integer.parseInt(attributes.getNamedItem("assertions").getNodeValue());
            }
            if (attributes.getNamedItem("errors") != null) {
                suite.errorCount = Integer.parseInt(attributes.getNamedItem("errors").getNodeValue());
            }
            if (attributes.getNamedItem("failures") != null) {
                suite.failureCount = Integer.parseInt(attributes.getNamedItem("failures").getNodeValue());
            }
            if (attributes.getNamedItem("time") != null) {
                suite.time = Double.parseDouble(attributes.getNamedItem("time").getNodeValue());
            }

            NodeList childNodes = node.getChildNodes();
            for (int i = 0, count = childNodes.getLength(); i < count; ++i) {
                TestResult result = scanNode(childNodes.item(i));
                if (result != null) {
                    suite.addTestResult(result);
                }
            }

            return suite;
        } else if (node.getNodeName().equals("testcase")) {
            TestCase testCase = new TestCase();
            testCase.name = attributes.getNamedItem("name").getNodeValue();
            if (attributes.getNamedItem("class") != null) {
                testCase.className = attributes.getNamedItem("class").getNodeValue();
            }
            if (attributes.getNamedItem("file") != null) {
                testCase.file = attributes.getNamedItem("file").getNodeValue();
            }
            if (attributes.getNamedItem("line") != null) {
                testCase.line = Integer.parseInt(attributes.getNamedItem("line").getNodeValue());
            }
            if (attributes.getNamedItem("assertions") != null) {
                testCase.assertionCount = Integer.parseInt(attributes.getNamedItem("assertions").getNodeValue());
            }
            if (attributes.getNamedItem("time") != null) {
                testCase.time = Double.parseDouble(attributes.getNamedItem("time").getNodeValue());
            }

            NodeList childNodes = node.getChildNodes();
            for (int i = 0, count = childNodes.getLength(); i < count; ++i) {
                if (childNodes.item(i).getNodeName().equals("failure")) {
                    Node failureNode = childNodes.item(i);

                    Failure failure = new Failure();
                    if (failureNode.getAttributes().getNamedItem("type") != null) {
                        failure.type = failureNode.getAttributes().getNamedItem("type").getNodeValue();
                    }
                    failure.content = failureNode.getTextContent();

                    testCase.failure = failure;
                }
            }

            return testCase;
        }
        return null;
    }
}
