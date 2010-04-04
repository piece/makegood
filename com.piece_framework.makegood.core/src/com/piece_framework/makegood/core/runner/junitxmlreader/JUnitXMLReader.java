package com.piece_framework.makegood.core.runner.junitxmlreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.piece_framework.makegood.core.runner.Result;
import com.piece_framework.makegood.core.runner.TestSuite;

public class JUnitXMLReader extends DefaultHandler {
    private File log;
    private boolean finished;
    private List<Result> results = new ArrayList<Result>();
    private TestSuite currentTestSuite;
    private TestCase currentTestCase;
    private StringBuilder contents;
    private List<JUnitXMLReaderListener> listeners = new ArrayList<JUnitXMLReaderListener>();
    private boolean stopped = false;
    private SynchronizedFileInputStream stream;

    public JUnitXMLReader(File log) {
        this.log = log;
    }

    public void start() throws ParserConfigurationException,
                               SAXException,
                               IOException {
        currentTestSuite = null;
        endTestCase();
        results = new ArrayList<Result>();

        if (!log.exists()) {
            log.createNewFile();
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        stream = new SynchronizedFileInputStream(log);
        parser.parse(stream, this);
    }

    public void stop() {
        stopped = true;
        finished = true;
    }

    public void addParserListener(JUnitXMLReaderListener listener) {
        listeners.add(listener);
    }

    public void removeParserListener(JUnitXMLReaderListener listener) {
        listeners.remove(listener);
    }

    public boolean isActive() {
        if (stream == null) {
            return false;
        }

        if (stream.closed) {
            return false;
        }

        if (finished) {
            return false;
        }

        return true;
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
        if (qualifiedName.equalsIgnoreCase("testsuite")) { //$NON-NLS-1$
            startTestSuite(createTestSuite(attributes));
        } else if (qualifiedName.equalsIgnoreCase("testcase")) { //$NON-NLS-1$
            startTestCase(createTestCase(attributes));
        } else if (qualifiedName.equalsIgnoreCase("failure")) { //$NON-NLS-1$
            Problem problem = new Problem(ProblemType.Failure);
            problem.setTypeClass(attributes.getValue("type")); //$NON-NLS-1$
            startProblem(problem);
            currentTestSuite.increaseFailureCount();
        } else if (qualifiedName.equalsIgnoreCase("error")) { //$NON-NLS-1$
            Problem problem = new Problem(ProblemType.Error);
            problem.setTypeClass(attributes.getValue("type")); //$NON-NLS-1$
            startProblem(problem);
            currentTestSuite.increaseErrorCount();
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
        if (qualifiedName.equalsIgnoreCase("testsuite")) { //$NON-NLS-1$
            endTestSuite();
        } else if (qualifiedName.equalsIgnoreCase("testcase")) { //$NON-NLS-1$
            endTestCase();
        } else if (qualifiedName.equalsIgnoreCase("failure")) { //$NON-NLS-1$
            endProblem();
        } else if (qualifiedName.equalsIgnoreCase("error")) { //$NON-NLS-1$
            endProblem();
        }
    }

    @Override
    public void endDocument() throws SAXException {
        finished = true;
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        if (stopped) {
            return;
        }

        super.error(e);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        if (stopped) {
            return;
        }

        super.fatalError(e);
    }

    public List<Result> getTestResults() {
        return Collections.unmodifiableList(results);
    }

    private void startTestSuite(TestSuite suite) {
        if (!results.isEmpty()) {
            currentTestSuite.addChild(suite);
        } else {
            results.add(suite);
        }

        currentTestSuite = suite;

        for (JUnitXMLReaderListener listener: listeners) {
            listener.startTestSuite(suite);
        }
    }

    private void endTestSuite() {
        if (currentTestSuite != null) {
            currentTestSuite = (TestSuite) currentTestSuite.getParent();
        }

        for (JUnitXMLReaderListener listener: listeners) {
            listener.endTestSuite();
        }
    }

    private void startTestCase(TestCase testCase) {
        currentTestSuite.addChild(testCase);
        currentTestCase = testCase;

        for (JUnitXMLReaderListener listener: listeners) {
            listener.startTestCase(testCase);
        }
    }

    private void endTestCase() {
        currentTestCase = null;

        for (JUnitXMLReaderListener listener: listeners) {
            listener.endTestCase();
        }
    }

    private void startProblem(Problem problem) {
        if (currentTestCase == null) {
            TestCase testCase =
                new TestCase("(" + problem.getType().toString().toLowerCase() + ")");
            testCase.setClassName(currentTestSuite.getName());
            testCase.setFile(currentTestSuite.getFile());
            testCase.setIsArtificial(true);
            startTestCase(testCase);
        }
        currentTestCase.setProblem(problem);

        contents = new StringBuilder();

        for (JUnitXMLReaderListener listener: listeners) {
            listener.startProblem(problem);
        }
    }

    private void endProblem() {
        currentTestCase.getProblem().setContent(contents.toString());

        for (JUnitXMLReaderListener listener: listeners) {
            listener.endProblem();
        }

        if (currentTestCase.isArtificial()) {
            endTestCase();
        }
    }

    private TestSuite createTestSuite(Attributes attributes) {
        TestSuite suite = new TestSuite(attributes.getValue("name")); //$NON-NLS-1$
        if (attributes.getIndex("file") != -1) { //$NON-NLS-1$
            suite.setFile(attributes.getValue("file")); //$NON-NLS-1$
        }
        if (attributes.getIndex("fullPackage") != -1) { //$NON-NLS-1$
            suite.setFullPackageName(attributes.getValue("fullPackage")); //$NON-NLS-1$
        }
        if (attributes.getIndex("package") != -1) { //$NON-NLS-1$
            suite.setPackageName(attributes.getValue("package")); //$NON-NLS-1$
        }
        if (attributes.getIndex("tests") != -1) { //$NON-NLS-1$
            suite.setTestCount(Integer.parseInt(attributes.getValue("tests"))); //$NON-NLS-1$
        }

        return suite;
    }

    private TestCase createTestCase(Attributes attributes) {
        TestCase testCase = new TestCase(attributes.getValue("name")); //$NON-NLS-1$
        if (attributes.getIndex("file") != -1) { //$NON-NLS-1$
            testCase.setFile(attributes.getValue("file")); //$NON-NLS-1$
        }
        if (attributes.getIndex("class") != -1) { //$NON-NLS-1$
            testCase.setClassName(attributes.getValue("class")); //$NON-NLS-1$
        }
        if (attributes.getIndex("line") != -1) { //$NON-NLS-1$
            testCase.setLine(Integer.parseInt(attributes.getValue("line"))); //$NON-NLS-1$
        }

        return testCase;
    }

    private class SynchronizedFileInputStream extends FileInputStream{
        private static final int READ_NO_PARAM = 1;
        private static final int READ_ARRAY = 2;
        private static final int READ_OFFSET = 3;
        boolean closed = false;

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
            closed = true;
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

                if (stopped) {
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
