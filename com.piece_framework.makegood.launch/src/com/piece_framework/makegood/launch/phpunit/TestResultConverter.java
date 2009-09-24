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
        if (!node.hasAttributes()) {
            return null;
        }

        if (node.getNodeName().equals("testsuite")) {
            TestSuite suite = new TestSuite(createAttributesMap(node));

            NodeList childNodes = node.getChildNodes();
            for (int i = 0, count = childNodes.getLength(); i < count; ++i) {
                TestResult result = scanNode(childNodes.item(i));
                if (result != null) {
                    suite.addTestResult(result);
                }
            }

            return suite;
        } else if (node.getNodeName().equals("testcase")) {
            TestCase testCase = new TestCase(createAttributesMap(node));

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

    private static Map<String, String> createAttributesMap(Node node) {
        Map<String, String> attributes = new HashMap<String, String>();
        for (int i = 0, count = node.getAttributes().getLength(); i < count; ++i) {
            attributes.put(node.getAttributes().item(i).getNodeName(),
                           node.getAttributes().item(i).getNodeValue()
                           );
        }
        return attributes;
    }
}
