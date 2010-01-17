package com.piece_framework.makegood.launch.elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TestResultParser extends DefaultHandler {
    private File log;
    private boolean wasEnd = true;
    private List<TestResult> results = new ArrayList<TestResult>();
    private TestSuite currentSuite;
    private TestCase currentCase;
    private StringBuilder contents;

    public TestResultParser(File log) {
        this.log = log;
    }

    public void start() throws ParserConfigurationException,
                               SAXException,
                               IOException {
        wasEnd = false;
        currentSuite = null;
        endTestCase();
        results = new ArrayList<TestResult>();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(log, this);
    }

    public boolean wasEnd() {
        return wasEnd;
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri,
                             String localName,
                             String qualifiedName,
                             Attributes attributes
                             ) throws SAXException {
        Map<String, String> map = convertAttributesToMap(attributes);

        if (qualifiedName.equalsIgnoreCase("testsuite")) {
            startTestSuite(map);
        } else if (qualifiedName.equalsIgnoreCase("testcase")) {
            startTestCase(map);
        } else if (qualifiedName.equalsIgnoreCase("failure")) {
            startProblem(map, ProblemType.Failure);
            currentSuite.increaseFailureCount();
        } else if (qualifiedName.equalsIgnoreCase("error")) {
            startProblem(map, ProblemType.Error);
            currentSuite.increaseErrorCount();
        }
    }

    @Override
    public void characters(char[] characters,
                           int start,
                           int length
                           ) throws SAXException {
        contents.append(new String(characters, start, length));
    }

    @Override
    public void endElement(String uri,
                           String localName,
                           String qualifiedName
                           ) throws SAXException {
        if (qualifiedName.equalsIgnoreCase("testsuite")) {
            endTestSuite();
        } else if (qualifiedName.equalsIgnoreCase("testcase")) {
            endTestCase();
        } else if (qualifiedName.equalsIgnoreCase("failure")) {
            endProblem();
        } else if (qualifiedName.equalsIgnoreCase("error")) {
            endProblem();
        }
    }

    @Override
    public void endDocument() throws SAXException {
        wasEnd = true;
    }

    public List<TestResult> getTestResults() {
        return Collections.unmodifiableList(results);
    }

    private Map<String, String> convertAttributesToMap(Attributes attributes) {
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < attributes.getLength(); ++i) {
            map.put(attributes.getQName(i),
                    attributes.getValue(i)
                    );
        }
        return map;
    }

    private void startTestSuite(Map<String, String> map) {
        TestSuite suite = new TestSuite(map);

        boolean isTop = currentSuite == null;
        if (!isTop) {
            currentSuite.addTestResult(suite);
        } else {
            results.add(suite);
        }

        currentSuite = suite;
    }

    private void endTestSuite() {
        if (currentSuite != null) {
            currentSuite = (TestSuite) currentSuite.parent;
        }
    }

    private void startTestCase(Map<String, String> map) {
        TestCase testCase = new TestCase(map);
        currentSuite.addTestResult(testCase);
        currentCase = testCase;
    }

    private void endTestCase() {
        currentCase = null;
    }

    private void startProblem(Map<String, String> map,
                              ProblemType problemType
                              ) {
        Problem problem = new Problem(problemType);
        problem.typeClass = map.get("type");

        if (currentCase == null) {
            Map<String, String> mapForTestCase = new HashMap<String, String>();
            mapForTestCase.put("name",
                               "(" + problemType.toString().toLowerCase() + ")");
            mapForTestCase.put("class", currentSuite.name);
            mapForTestCase.put("file", currentSuite.file);
            startTestCase(mapForTestCase);
        }
        currentCase.problem = problem;

        contents = new StringBuilder();
    }

    private void endProblem() {
        currentCase.problem.content = contents.toString();
    }
}
