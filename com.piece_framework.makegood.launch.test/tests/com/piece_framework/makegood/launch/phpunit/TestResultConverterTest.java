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
    public void convertAllResultType() {
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

        assertEquals(1, suite1.getTestResults().size());
        TestCase testCase1_1 = (TestCase) suite1.findTestResult("testTestShouldBeError");
        assertEquals("testTestShouldBeError", testCase1_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitErrorTest", testCase1_1.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitErrorTest.php",
                     testCase1_1.getFile()
                     );
        assertEquals(80, testCase1_1.getLine());
        assertEquals(0, testCase1_1.getAssertionCount());
        assertEquals(0.003933, testCase1_1.getTime(), 0.001);
        assertNull(testCase1_1.getFailure());

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

        assertEquals(1, suite2.getTestResults().size());
        TestCase testCase2_1 = (TestCase) suite2.findTestResult("testTestShouldPass");
        assertEquals("testTestShouldPass", testCase2_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitTest_PHPUnitPassTest", testCase2_1.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitTest/PHPUnitPassTest.php",
                     testCase2_1.getFile()
                     );
        assertEquals(80, testCase2_1.getLine());
        assertEquals(1, testCase2_1.getAssertionCount());
        assertEquals(0.003306, testCase2_1.getTime(), 0.001);
        assertNull(testCase2_1.getFailure());

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

        assertEquals(0, suite3.getTestResults().size());

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

        assertEquals(1, suite4.getTestResults().size());
        TestCase testCase4_1 = (TestCase) suite4.findTestResult("testTestShouldBeFailure");
        assertEquals("testTestShouldBeFailure", testCase4_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitFailureTest", testCase4_1.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitFailureTest.php",
                     testCase4_1.getFile()
                     );
        assertEquals(80, testCase4_1.getLine());
        assertEquals(1, testCase4_1.getAssertionCount());
        assertEquals(0.003452, testCase4_1.getTime(), 0.001);
        assertEquals("PHPUnit_Framework_ExpectationFailedException", testCase4_1.getFailure().getType());
        assertEquals("Stagehand_TestRunner_PHPUnitFailureTest::testTestShouldBeFailure\n" +
"This is an error message.\n" +
"Failed asserting that <boolean:false> is true.\n" +
"\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitFailureTest.php:82\n",
                     testCase4_1.getFailure().getContent()
                     );

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

        assertEquals(3, suite5.getTestResults().size());
        TestCase testCase5_1 = (TestCase) suite5.findTestResult("testTestShouldPass1");
        assertEquals("testTestShouldPass1", testCase5_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase5_1.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase5_1.getFile()
                     );
        assertEquals(80, testCase5_1.getLine());
        assertEquals(1, testCase5_1.getAssertionCount());
        assertEquals(0.003225, testCase5_1.getTime(), 0.001);
        assertNull(testCase5_1.getFailure());

        TestCase testCase5_2 = (TestCase) suite5.findTestResult("testTestShouldPass2");
        assertEquals("testTestShouldPass2", testCase5_2.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase5_2.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase5_2.getFile()
                     );
        assertEquals(85, testCase5_2.getLine());
        assertEquals(1, testCase5_2.getAssertionCount());
        assertEquals(0.003196, testCase5_2.getTime(), 0.001);
        assertNull(testCase5_2.getFailure());

        TestCase testCase5_3 = (TestCase) suite5.findTestResult("test日本語を使用できること");
        assertEquals("test日本語を使用できること", testCase5_3.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase5_3.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase5_3.getFile()
                     );
        assertEquals(90, testCase5_3.getLine());
        assertEquals(1, testCase5_3.getAssertionCount());
        assertEquals(0.003264, testCase5_3.getTime(), 0.001);
        assertNull(testCase5_3.getFailure());

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

        assertEquals(0, suite6.getTestResults().size());

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

        assertEquals(2, suite7.getTestResults().size());
        TestCase testCase7_1 = (TestCase) suite7.findTestResult("testTestShouldPassExtended");
        assertEquals("testTestShouldPassExtended", testCase7_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitExtendedTest", testCase7_1.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitExtendedTest.php",
                     testCase7_1.getFile()
                     );
        assertEquals(82, testCase7_1.getLine());
        assertEquals(1, testCase7_1.getAssertionCount());
        assertEquals(0.003204, testCase7_1.getTime(), 0.001);
        assertNull(testCase7_1.getFailure());

        TestCase testCase7_2 = (TestCase) suite7.findTestResult("testTestShouldPassCommon");
        assertEquals("testTestShouldPassCommon", testCase7_2.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitExtendedTest", testCase7_2.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitExtendedTest.php",
                     testCase7_2.getFile()
                     );
        assertEquals(80, testCase7_2.getLine());
        assertEquals(1, testCase7_2.getAssertionCount());
        assertEquals(0.003159, testCase7_2.getTime(), 0.001);
        assertNull(testCase7_2.getFailure());

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

        assertEquals(0, suite8.getTestResults().size());

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

        assertEquals(1, suite9.getTestResults().size());
        TestCase testCase9_1 = (TestCase) suite9.findTestResult("pass");
        assertEquals("pass", testCase9_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitDependsTest", testCase9_1.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitDependsTest.php",
                     testCase9_1.getFile()
                     );
        assertEquals(83, testCase9_1.getLine());
        assertEquals(1, testCase9_1.getAssertionCount());
        assertEquals(0.003414, testCase9_1.getTime(), 0.001);
        assertEquals("PHPUnit_Framework_ExpectationFailedException", testCase9_1.getFailure().getType());
        assertEquals("Stagehand_TestRunner_PHPUnitDependsTest::pass\n" +
"Failed asserting that <boolean:false> is true.\n" +
"\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitDependsTest.php:85\n",
                     testCase9_1.getFailure().getContent()
                     );

    }

    @Test
    public void convertPassResult() {
        List<TestSuite> testResults = TestResultConverter.convert(new File(System.getProperty("user.dir") +
                                                                            String.valueOf(File.separatorChar) +
                                                                            "phpunit-results/pass.log"
                                                                            ));

        assertEquals(1, testResults.size());

        TestSuite rootSuite = testResults.get(0);
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", rootSuite.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     rootSuite.getFile()
                     );
        assertEquals("Stagehand_TestRunner", rootSuite.getFullPackage());
        assertEquals("Stagehand_TestRunner", rootSuite.getPackageName());
        assertEquals(3, rootSuite.getTestCount());
        assertEquals(3, rootSuite.getAssertionCount());
        assertEquals(0, rootSuite.getErrorCount());
        assertEquals(0, rootSuite.getFailureCount());
        assertEquals(0.022199, rootSuite.getTime(), 0.001);

        assertEquals(3, rootSuite.getTestResults().size());
        TestCase testCase1 = (TestCase) rootSuite.findTestResult("testTestShouldPass1");
        assertEquals("testTestShouldPass1", testCase1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase1.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase1.getFile()
                     );
        assertEquals(80, testCase1.getLine());
        assertEquals(1, testCase1.getAssertionCount());
        assertEquals(0.003642, testCase1.getTime(), 0.001);
        assertNull(testCase1.getFailure());

        TestCase testCase2 = (TestCase) rootSuite.findTestResult("testTestShouldPass2");
        assertEquals("testTestShouldPass2", testCase2.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase2.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase2.getFile()
                     );
        assertEquals(85, testCase2.getLine());
        assertEquals(1, testCase2.getAssertionCount());
        assertEquals(0.015340, testCase2.getTime(), 0.001);
        assertNull(testCase2.getFailure());

        TestCase testCase3 = (TestCase) rootSuite.findTestResult("test日本語を使用できること");
        assertEquals("test日本語を使用できること", testCase3.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase3.getClassName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase3.getFile()
                     );
        assertEquals(90, testCase3.getLine());
        assertEquals(1, testCase3.getAssertionCount());
        assertEquals(0.003217, testCase3.getTime(), 0.001);
        assertNull(testCase3.getFailure());
    }
}
