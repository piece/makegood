package com.piece_framework.makegood.core.runner.junitxmlreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.piece_framework.makegood.core.runner.Problem;
import com.piece_framework.makegood.core.runner.ProblemType;
import com.piece_framework.makegood.core.runner.TestCase;
import com.piece_framework.makegood.core.runner.TestResult;
import com.piece_framework.makegood.core.runner.TestSuite;

public class JUnitXMLReader extends DefaultHandler {
    private File log;
    private boolean wasEnd;
    private List<TestResult> results = new ArrayList<TestResult>();
    private TestSuite currentSuite;
    private TestCase currentCase;
    private StringBuilder contents;
    private List<JUnitXMLReaderListener> listeners = new ArrayList<JUnitXMLReaderListener>();
    private boolean stop = false;
    private SynchronizedFileInputStream stream;
    private boolean createdTestCase;

    public JUnitXMLReader(File log) {
        this.log = log;
    }

    public void start() throws ParserConfigurationException,
                               SAXException,
                               IOException {
        currentSuite = null;
        endTestCase();
        results = new ArrayList<TestResult>();

        if (!log.exists()) {
            log.createNewFile();
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        stream = new SynchronizedFileInputStream(log);
        parser.parse(stream, this);
    }

    public void stop() {
        stop = true;
        wasEnd = true;
    }

    public void addParserListener(JUnitXMLReaderListener listener) {
        listeners.add(listener);
    }

    public void removeParserListener(JUnitXMLReaderListener listener) {
        listeners.remove(listener);
    }

    public boolean wasEnd() {
        return wasEnd &&
               (stream != null || stream.isClose);
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
        if (contents != null) {
            contents.append(new String(characters, start, length));
        }
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

    @Override
    public void error(SAXParseException e) throws SAXException {
        if (stop) {
            return;
        }
        super.error(e);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        if (stop) {
            return;
        }
        super.fatalError(e);
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

        for (JUnitXMLReaderListener listener: listeners) {
            listener.startTestSuite(suite);
        }
    }

    private void endTestSuite() {
        if (currentSuite != null) {
            currentSuite = (TestSuite) currentSuite.getParent();
        }

        for (JUnitXMLReaderListener listener: listeners) {
            listener.endTestSuite();
        }
    }

    private void startTestCase(Map<String, String> map) {
        TestCase testCase = new TestCase(map);
        currentSuite.addTestResult(testCase);
        currentCase = testCase;

        for (JUnitXMLReaderListener listener: listeners) {
            listener.startTestCase(testCase);
        }
    }

    private void endTestCase() {
        currentCase = null;

        for (JUnitXMLReaderListener listener: listeners) {
            listener.endTestCase();
        }
    }

    private void startProblem(Map<String, String> map,
                              ProblemType problemType
                              ) {
        Problem problem = new Problem(problemType);
        problem.setTypeClass(map.get("type"));

        if (currentCase == null) {
            Map<String, String> mapForTestCase = new HashMap<String, String>();
            mapForTestCase.put("name",
                               "(" + problemType.toString().toLowerCase() + ")");
            mapForTestCase.put("class", currentSuite.getName());
            mapForTestCase.put("file", currentSuite.getFile());
            startTestCase(mapForTestCase);
            createdTestCase = true;
        }
        currentCase.setProblem(problem);

        contents = new StringBuilder();

        for (JUnitXMLReaderListener listener: listeners) {
            listener.startProblem(problem);
        }
    }

    private void endProblem() {
        currentCase.getProblem().setContent(contents.toString());

        for (JUnitXMLReaderListener listener: listeners) {
            listener.endProblem();
        }

        if (createdTestCase) {
            endTestCase();
            createdTestCase = false;
        }
    }

    private class SynchronizedFileInputStream extends FileInputStream{
        private static final int READ_NO_PARAM = 1;
        private static final int READ_ARRAY = 2;
        private static final int READ_OFFSET = 3;
        boolean isClose = false;

        public SynchronizedFileInputStream(File file) throws FileNotFoundException {
            super(file);
        }

        @Override
        public int read() throws IOException {
            return read(READ_NO_PARAM, null, 0, 0);
        }

        @Override
        public int read(byte[] bytes,
                        int offset,
                        int length
                        ) throws IOException {
            return read(READ_OFFSET, bytes, offset, length);
        }

        @Override
        public int read(byte[] bytes) throws IOException {
            return read(READ_ARRAY, bytes, 0, 0);
        }

        @Override
        public void close() throws IOException {
            isClose = true;
            super.close();
        }

        private int read(int readType,
                         byte[] bytes,
                         int offset,
                         int length
                         ) throws IOException {
            int result = -1;
            do {
                if (readType == READ_NO_PARAM) {
                    result = super.read();
                } else if (readType == READ_ARRAY) {
                    result = super.read(bytes);
                } else if (readType == READ_OFFSET) {
                    result = super.read(bytes, offset, length);
                }
                if (result != -1) {
                    break;
                }

                if (stop) {
                    break;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {}
            } while (true);
            return result;
        }
    }
}
