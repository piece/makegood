package com.piece_framework.makegood.launch.phpunit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestResultConverterTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void convertAllResult() {
        List<TestSuite> testResults = TestResultConverter.convert(new File(System.getProperty("user.dir") +
                                                                            String.valueOf(File.separatorChar) +
                                                                            "phpunit-results/all.log"
                                                                            ));

        assertEquals(1, testResults.size());

        TestSuite suite = testResults.get(0);
        assertEquals("tests/Stagehand/TestRunner", suite.getName());
        assertNull(suite.getFile());
        assertNull(suite.getPackageName());
        assertEquals(9, suite.getTestCount());
        assertEquals(8, suite.getAssertionCount());
        assertEquals(0, suite.getErrorCount());
        assertEquals(2, suite.getFailureCount());
        assertEquals(0.030153, suite.getTime(), 0.01);
    }
}
