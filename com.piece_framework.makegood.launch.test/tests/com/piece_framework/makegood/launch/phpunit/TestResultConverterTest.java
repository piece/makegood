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

        TestSuite rootSuite = testResults.get(0);
        assertEquals("tests/Stagehand/TestRunner", rootSuite.getName());
        assertNull(rootSuite.getFile());
        assertNull(rootSuite.getFullPackage());
        assertNull(rootSuite.getPackageName());
        assertEquals(9, rootSuite.getTestCount());
        assertEquals(8, rootSuite.getAssertionCount());
        assertEquals(0, rootSuite.getErrorCount());
        assertEquals(2, rootSuite.getFailureCount());
        assertEquals(0.030153, rootSuite.getTime(), 0.001);

        assertEquals(9, rootSuite.getTestResults().size());

        TestSuite suite1 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitErrorTest");
        assertEquals("Stagehand_TestRunner_PHPUnitErrorTest", suite1.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitErrorTest.php",
                     suite1.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite1.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite1.getPackageName());
        assertEquals(1, suite1.getTestCount());
        assertEquals(0, suite1.getAssertionCount());
        assertEquals(0, suite1.getErrorCount());
        assertEquals(0, suite1.getFailureCount());
        assertEquals(0.003933, suite1.getTime(), 0.001);

        TestSuite suite2 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitTest_PHPUnitPassTest");
        assertEquals("Stagehand_TestRunner_PHPUnitTest_PHPUnitPassTest", suite2.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitTest/PHPUnitPassTest.php",
                     suite2.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite2.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite2.getPackageName());
        assertEquals(1, suite2.getTestCount());
        assertEquals(1, suite2.getAssertionCount());
        assertEquals(0, suite2.getErrorCount());
        assertEquals(0, suite2.getFailureCount());
        assertEquals(0.003306, suite2.getTime(), 0.001);

        TestSuite suite3 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitImcompleteTest");
        assertEquals("Stagehand_TestRunner_PHPUnitImcompleteTest", suite3.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitIncompleteTest.php",
                     suite3.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite3.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite3.getPackageName());
        assertEquals(0, suite3.getTestCount());
        assertEquals(0, suite3.getAssertionCount());
        assertEquals(0, suite3.getErrorCount());
        assertEquals(0, suite3.getFailureCount());
        assertEquals(0.000000, suite3.getTime(), 0.001);

        TestSuite suite4 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitFailureTest");
        assertEquals("Stagehand_TestRunner_PHPUnitFailureTest", suite4.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitFailureTest.php",
                     suite4.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite4.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite4.getPackageName());
        assertEquals(1, suite4.getTestCount());
        assertEquals(1, suite4.getAssertionCount());
        assertEquals(0, suite4.getErrorCount());
        assertEquals(1, suite4.getFailureCount());
        assertEquals(0.003452, suite4.getTime(), 0.001);

        TestSuite suite5 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitPassTest");
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", suite5.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     suite5.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite5.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite5.getPackageName());
        assertEquals(3, suite5.getTestCount());
        assertEquals(3, suite5.getAssertionCount());
        assertEquals(0, suite5.getErrorCount());
        assertEquals(0, suite5.getFailureCount());
        assertEquals(0.009685, suite5.getTime(), 0.001);

        TestSuite suite6 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitNoTestsTest");
        assertEquals("Stagehand_TestRunner_PHPUnitNoTestsTest", suite6.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitNoTestsTest.php",
                     suite6.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite6.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite6.getPackageName());
        assertEquals(0, suite6.getTestCount());
        assertEquals(0, suite6.getAssertionCount());
        assertEquals(0, suite6.getErrorCount());
        assertEquals(0, suite6.getFailureCount());
        assertEquals(0.000000, suite6.getTime(), 0.001);

        TestSuite suite7 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitExtendedTest");
        assertEquals("Stagehand_TestRunner_PHPUnitExtendedTest", suite7.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitExtendedTest.php",
                     suite7.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite7.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite7.getPackageName());
        assertEquals(2, suite7.getTestCount());
        assertEquals(2, suite7.getAssertionCount());
        assertEquals(0, suite7.getErrorCount());
        assertEquals(0, suite7.getFailureCount());
        assertEquals(0.006363, suite7.getTime(), 0.001);

        TestSuite suite8 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitSkippedTest");
        assertEquals("Stagehand_TestRunner_PHPUnitSkippedTest", suite8.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitSkippedTest.php",
                     suite8.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite8.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite8.getPackageName());
        assertEquals(0, suite8.getTestCount());
        assertEquals(0, suite8.getAssertionCount());
        assertEquals(0, suite8.getErrorCount());
        assertEquals(0, suite8.getFailureCount());
        assertEquals(0.000000, suite8.getTime(), 0.001);

        TestSuite suite9 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitDependsTest");
        assertEquals("Stagehand_TestRunner_PHPUnitDependsTest", suite9.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitDependsTest.php",
                     suite9.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite9.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite9.getPackageName());
        assertEquals(1, suite9.getTestCount());
        assertEquals(1, suite9.getAssertionCount());
        assertEquals(0, suite9.getErrorCount());
        assertEquals(1, suite9.getFailureCount());
        assertEquals(0.003414, suite9.getTime(), 0.001);
    }
}
