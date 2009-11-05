package com.piece_framework.makegood.launch.phpunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestResultConverterTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void convertAllResultType() throws Exception {
        List<TestSuite> testResults = TestResultConverter.convert(new File(System.getProperty("user.dir") +
                                                                            String.valueOf(File.separatorChar) +
                                                                            "phpunit-results/alltests.log"
                                                                            ));

        assertEquals(1, testResults.size());

        TestSuite rootSuite = testResults.get(0);
        assertEquals("", rootSuite.getName());
        assertNull(rootSuite.getFile());
        assertNull(rootSuite.getFullPackage());
        assertNull(rootSuite.getPackageName());
        assertEquals(12, rootSuite.getTestCount());
        assertEquals(8, rootSuite.getAssertionCount());
        assertEquals(4, rootSuite.getErrorCount());
        assertEquals(2, rootSuite.getFailureCount());
        assertEquals(0.043255, rootSuite.getTime(), 0.0);

        assertEquals(9, rootSuite.getTestResults().size());

        TestSuite suite1 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitDependsTest");
        assertEquals("Stagehand_TestRunner_PHPUnitDependsTest", suite1.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitDependsTest.php",
                     suite1.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite1.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite1.getPackageName());
        assertEquals(2, suite1.getTestCount());
        assertEquals(1, suite1.getAssertionCount());
        assertEquals(1, suite1.getErrorCount());
        assertEquals(1, suite1.getFailureCount());
        assertEquals(0.004813, suite1.getTime(), 0.0);

        assertEquals(2, suite1.getTestResults().size());
        TestCase testCase1_1 = (TestCase) suite1.findTestResult("pass");
        assertEquals("pass", testCase1_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitDependsTest", testCase1_1.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitDependsTest.php",
                     testCase1_1.getFile()
                     );
        assertEquals(83, testCase1_1.getLine());
        assertEquals(1, testCase1_1.getAssertionCount());
        assertEquals(0.004792, testCase1_1.getTime(), 0.0);
        assertEquals(ProblemType.Failure, testCase1_1.getProblem().getType());
        assertEquals("PHPUnit_Framework_ExpectationFailedException", testCase1_1.getProblem().getTypeClass());
        assertEquals("Stagehand_TestRunner_PHPUnitDependsTest::pass\n" +
"Failed asserting that <boolean:false> is true.\n" +
"\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitDependsTest.php:85\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner/Runner/PHPUnitRunner.php:120\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner.php:366\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner.php:175\n" +
"/home/iteman/site-php/5.2/PEAR/src/Stagehand/CLIController.php:101\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/bin/phpunitrunner:50\n", testCase1_1.getProblem().getContent());

        TestCase testCase1_2 = (TestCase) suite1.findTestResult("skip");
        assertEquals("skip", testCase1_2.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitDependsTest", testCase1_2.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitDependsTest.php",
                     testCase1_2.getFile()
                     );
        assertEquals(92, testCase1_2.getLine());
        assertEquals(0, testCase1_2.getAssertionCount());
        assertEquals(0.000021, testCase1_2.getTime(), 0.0);
        assertEquals(ProblemType.Error, testCase1_2.getProblem().getType());
        assertEquals("PHPUnit_Framework_SkippedTestError", testCase1_2.getProblem().getTypeClass());
        assertEquals("Skipped Test\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner/Runner/PHPUnitRunner.php:120\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner.php:366\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner.php:175\n" +
"/home/iteman/site-php/5.2/PEAR/src/Stagehand/CLIController.php:101\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/bin/phpunitrunner:50\n",
                     testCase1_2.getProblem().getContent());

        TestSuite suite2 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitErrorTest");
        assertEquals("Stagehand_TestRunner_PHPUnitErrorTest", suite2.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitErrorTest.php",
                     suite2.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite2.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite2.getPackageName());
        assertEquals(1, suite2.getTestCount());
        assertEquals(0, suite2.getAssertionCount());
        assertEquals(1, suite2.getErrorCount());
        assertEquals(0, suite2.getFailureCount());
        assertEquals(0.004186, suite2.getTime(), 0.0);

        assertEquals(1, suite2.getTestResults().size());
        TestCase testCase2_1 = (TestCase) suite2.findTestResult("testTestShouldBeError");
        assertEquals("testTestShouldBeError", testCase2_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitErrorTest", testCase2_1.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitErrorTest.php",
                     testCase2_1.getFile()
                     );
        assertEquals(80, testCase2_1.getLine());
        assertEquals(0, testCase2_1.getAssertionCount());
        assertEquals(0.004186, testCase2_1.getTime(), 0.0);
        assertEquals(ProblemType.Error, testCase2_1.getProblem().getType());
        assertEquals("PHPUnit_Framework_Error_Notice", testCase2_1.getProblem().getTypeClass());
        assertEquals("Stagehand_TestRunner_PHPUnitErrorTest::testTestShouldBeError\n" +
"Undefined property: Stagehand_TestRunner_PHPUnitErrorTest::$foo\n" +
"\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitErrorTest.php:82\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner/Runner/PHPUnitRunner.php:120\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner.php:366\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner.php:175\n" +
"/home/iteman/site-php/5.2/PEAR/src/Stagehand/CLIController.php:101\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/bin/phpunitrunner:50\n",
                     testCase2_1.getProblem().getContent());

        TestSuite suite3 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitExtendedTest");
        assertEquals("Stagehand_TestRunner_PHPUnitExtendedTest", suite3.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitExtendedTest.php",
                     suite3.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite3.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite3.getPackageName());
        assertEquals(2, suite3.getTestCount());
        assertEquals(2, suite3.getAssertionCount());
        assertEquals(0, suite3.getErrorCount());
        assertEquals(0, suite3.getFailureCount());
        assertEquals(0.007455, suite3.getTime(), 0.0);

        assertEquals(2, suite3.getTestResults().size());

        assertEquals(2, suite3.getTestResults().size());
        TestCase testCase3_1 = (TestCase) suite3.findTestResult("testTestShouldPassExtended");
        assertEquals("testTestShouldPassExtended", testCase3_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitExtendedTest", testCase3_1.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitExtendedTest.php",
                     testCase3_1.getFile()
                     );
        assertEquals(82, testCase3_1.getLine());
        assertEquals(1, testCase3_1.getAssertionCount());
        assertEquals(0.003853, testCase3_1.getTime(), 0.0);
        assertEquals(ProblemType.Pass, testCase3_1.getProblem().getType());

        TestCase testCase3_2 = (TestCase) suite3.findTestResult("testTestShouldPassCommon");
        assertEquals("testTestShouldPassCommon", testCase3_2.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitExtendedTest", testCase3_2.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitExtendedTest.php",
                     testCase3_2.getFile()
                     );
        assertEquals(80, testCase3_2.getLine());
        assertEquals(1, testCase3_2.getAssertionCount());
        assertEquals(0.003602, testCase3_2.getTime(), 0.0);
        assertEquals(ProblemType.Pass, testCase3_2.getProblem().getType());

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
        assertEquals(0.003942, suite4.getTime(), 0.0);

        assertEquals(1, suite4.getTestResults().size());
        TestCase testCase4_1 = (TestCase) suite4.findTestResult("testTestShouldBeFailure");
        assertEquals("testTestShouldBeFailure", testCase4_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitFailureTest", testCase4_1.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitFailureTest.php",
                     testCase4_1.getFile()
                     );
        assertEquals(80, testCase4_1.getLine());
        assertEquals(1, testCase4_1.getAssertionCount());
        assertEquals(0.003942, testCase4_1.getTime(), 0.0);
        assertEquals(ProblemType.Failure, testCase4_1.getProblem().getType());
        assertEquals("PHPUnit_Framework_ExpectationFailedException", testCase4_1.getProblem().getTypeClass());
        assertEquals("Stagehand_TestRunner_PHPUnitFailureTest::testTestShouldBeFailure\n" +
"This is an error message.\n" +
"Failed asserting that <boolean:false> is true.\n" +
"\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitFailureTest.php:82\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner/Runner/PHPUnitRunner.php:120\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner.php:366\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner.php:175\n" +
"/home/iteman/site-php/5.2/PEAR/src/Stagehand/CLIController.php:101\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/bin/phpunitrunner:50\n",
                     testCase4_1.getProblem().getContent()
                     );

        TestSuite suite5 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitImcompleteTest");
        assertEquals("Stagehand_TestRunner_PHPUnitImcompleteTest", suite5.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitIncompleteTest.php",
                     suite5.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite5.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite5.getPackageName());
        assertEquals(1, suite5.getTestCount());
        assertEquals(0, suite5.getAssertionCount());
        assertEquals(1, suite5.getErrorCount());
        assertEquals(0, suite5.getFailureCount());
        assertEquals(0.003850, suite5.getTime(), 0.0);

        assertEquals(1, suite5.getTestResults().size());
        TestCase testCase5_1 = (TestCase) suite5.findTestResult("testTestShouldBeImcomplete");
        assertEquals("testTestShouldBeImcomplete", testCase5_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitImcompleteTest", testCase5_1.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitIncompleteTest.php",
                     testCase5_1.getFile()
                     );
        assertEquals(80, testCase5_1.getLine());
        assertEquals(0, testCase5_1.getAssertionCount());
        assertEquals(0.003850, testCase5_1.getTime(), 0.0);
        assertEquals(ProblemType.Error, testCase5_1.getProblem().getType());
        assertEquals("PHPUnit_Framework_IncompleteTestError", testCase5_1.getProblem().getTypeClass());
        assertEquals("Incomplete Test\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitIncompleteTest.php:82\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner/Runner/PHPUnitRunner.php:120\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner.php:366\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/src/Stagehand/TestRunner.php:175\n" +
"/home/iteman/site-php/5.2/PEAR/src/Stagehand/CLIController.php:101\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/bin/phpunitrunner:50\n",
                     testCase5_1.getProblem().getContent());

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
        assertEquals(0.000000, suite6.getTime(), 0.0);

        assertEquals(0, suite6.getTestResults().size());

        TestSuite suite7 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitPassTest");
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", suite7.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     suite7.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite7.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite7.getPackageName());
        assertEquals(3, suite7.getTestCount());
        assertEquals(3, suite7.getAssertionCount());
        assertEquals(0, suite7.getErrorCount());
        assertEquals(0, suite7.getFailureCount());
        assertEquals(0.011314, suite7.getTime(), 0.0);

        assertEquals(3, suite7.getTestResults().size());
        TestCase testCase7_1 = (TestCase) suite7.findTestResult("testTestShouldPass1");
        assertEquals("testTestShouldPass1", testCase7_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase7_1.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase7_1.getFile()
                     );
        assertEquals(80, testCase7_1.getLine());
        assertEquals(1, testCase7_1.getAssertionCount());
        assertEquals(0.003600, testCase7_1.getTime(), 0.0);
        assertEquals(ProblemType.Pass, testCase7_1.getProblem().getType());

        TestCase testCase7_2 = (TestCase) suite7.findTestResult("testTestShouldPass2");
        assertEquals("testTestShouldPass2", testCase7_2.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase7_2.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase7_2.getFile()
                     );
        assertEquals(85, testCase7_2.getLine());
        assertEquals(1, testCase7_2.getAssertionCount());
        assertEquals(0.003819, testCase7_2.getTime(), 0.0);
        assertEquals(ProblemType.Pass, testCase7_2.getProblem().getType());

        TestCase testCase7_3 = (TestCase) suite7.findTestResult("test日本語を使用できること");
        assertEquals("test日本語を使用できること", testCase7_3.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase7_3.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase7_3.getFile()
                     );
        assertEquals(90, testCase7_3.getLine());
        assertEquals(1, testCase7_3.getAssertionCount());
        assertEquals(0.003895, testCase7_3.getTime(), 0.0);
        assertEquals(ProblemType.Pass, testCase7_3.getProblem().getType());

        TestSuite suite8 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitSkippedTest");
        assertEquals("Stagehand_TestRunner_PHPUnitSkippedTest", suite8.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitSkippedTest.php",
                     suite8.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite8.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite8.getPackageName());
        assertEquals(1, suite8.getTestCount());
        assertEquals(0, suite8.getAssertionCount());
        assertEquals(1, suite8.getErrorCount());
        assertEquals(0, suite8.getFailureCount());
        assertEquals(0.004022, suite8.getTime(), 0.0);

        assertEquals(1, suite8.getTestResults().size());

        TestSuite suite9 = (TestSuite) rootSuite.findTestResult("Stagehand_TestRunner_PHPUnitTest_PHPUnitPassTest");
        assertEquals("Stagehand_TestRunner_PHPUnitTest_PHPUnitPassTest", suite9.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitTest/PHPUnitPassTest.php",
                     suite9.getFile()
                     );
        assertEquals("Stagehand_TestRunner", suite9.getFullPackage());
        assertEquals("Stagehand_TestRunner", suite9.getPackageName());
        assertEquals(1, suite9.getTestCount());
        assertEquals(1, suite9.getAssertionCount());
        assertEquals(0, suite9.getErrorCount());
        assertEquals(0, suite9.getFailureCount());
        assertEquals(0.003673, suite9.getTime(), 0.0);

        assertEquals(1, suite9.getTestResults().size());
        TestCase testCase9_1 = (TestCase) suite9.findTestResult("testTestShouldPass");
        assertEquals("testTestShouldPass", testCase9_1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitTest_PHPUnitPassTest", testCase9_1.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitTest/PHPUnitPassTest.php",
                     testCase9_1.getFile()
                     );
        assertEquals(80, testCase9_1.getLine());
        assertEquals(1, testCase9_1.getAssertionCount());
        assertEquals(0.003673, testCase9_1.getTime(), 0.0);
        assertEquals(ProblemType.Pass, testCase9_1.getProblem().getType());
    }

    @Test
    public void convertResultThatOneTestClassPass() throws Exception {
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
        assertEquals(0.022199, rootSuite.getTime(), 0.0);

        assertEquals(3, rootSuite.getTestResults().size());
        TestCase testCase1 = (TestCase) rootSuite.findTestResult("testTestShouldPass1");
        assertEquals("testTestShouldPass1", testCase1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase1.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase1.getFile()
                     );
        assertEquals(80, testCase1.getLine());
        assertEquals(1, testCase1.getAssertionCount());
        assertEquals(0.003642, testCase1.getTime(), 0.0);
        assertEquals(ProblemType.Pass, testCase1.getProblem().getType());

        TestCase testCase2 = (TestCase) rootSuite.findTestResult("testTestShouldPass2");
        assertEquals("testTestShouldPass2", testCase2.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase2.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase2.getFile()
                     );
        assertEquals(85, testCase2.getLine());
        assertEquals(1, testCase2.getAssertionCount());
        assertEquals(0.015340, testCase2.getTime(), 0.0);
        assertEquals(ProblemType.Pass, testCase2.getProblem().getType());

        TestCase testCase3 = (TestCase) rootSuite.findTestResult("test日本語を使用できること");
        assertEquals("test日本語を使用できること", testCase3.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitPassTest", testCase3.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitPassTest.php",
                     testCase3.getFile()
                     );
        assertEquals(90, testCase3.getLine());
        assertEquals(1, testCase3.getAssertionCount());
        assertEquals(0.003217, testCase3.getTime(), 0.0);
        assertEquals(ProblemType.Pass, testCase3.getProblem().getType());
    }

    @Test
    public void convertResultThatOneTestClassIsFailure() throws Exception {
        List<TestSuite> testResults = TestResultConverter.convert(new File(System.getProperty("user.dir") +
                                                                            String.valueOf(File.separatorChar) +
                                                                            "phpunit-results/failure.log"
                                                                            ));

        assertEquals(1, testResults.size());

        TestSuite rootSuite = testResults.get(0);
        assertEquals("Stagehand_TestRunner_PHPUnitFailureTest", rootSuite.getName());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitFailureTest.php",
                     rootSuite.getFile()
                     );
        assertEquals("Stagehand_TestRunner", rootSuite.getFullPackage());
        assertEquals("Stagehand_TestRunner", rootSuite.getPackageName());
        assertEquals(1, rootSuite.getTestCount());
        assertEquals(1, rootSuite.getAssertionCount());
        assertEquals(0, rootSuite.getErrorCount());
        assertEquals(1, rootSuite.getFailureCount());
        assertEquals(0.003905, rootSuite.getTime(), 0.0);

        assertEquals(1, rootSuite.getTestResults().size());
        TestCase testCase1 = (TestCase) rootSuite.findTestResult("testTestShouldBeFailure");
        assertEquals("testTestShouldBeFailure", testCase1.getName());
        assertEquals("Stagehand_TestRunner_PHPUnitFailureTest", testCase1.getTargetClass());
        assertEquals("/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitFailureTest.php",
                     testCase1.getFile()
                     );
        assertEquals(80, testCase1.getLine());
        assertEquals(1, testCase1.getAssertionCount());
        assertEquals(0.003905, testCase1.getTime(), 0.0);
        assertEquals(ProblemType.Failure, testCase1.getProblem().getType());
        assertEquals("PHPUnit_Framework_ExpectationFailedException", testCase1.getProblem().getTypeClass());
        assertEquals("Stagehand_TestRunner_PHPUnitFailureTest::testTestShouldBeFailure\n" +
"This is an error message.\n" +
"Failed asserting that <boolean:false> is true.\n" +
"\n" +
"/home/iteman/GITREPOS/stagehand-testrunner/tests/Stagehand/TestRunner/PHPUnitFailureTest.php:82\n",
                     testCase1.getProblem().getContent()
                     );
    }

    @Test(expected=FileNotFoundException.class)
    public void checkResultFile() throws Exception {
        TestResultConverter.convert(new File(System.getProperty("user.dir") +
                                              String.valueOf(File.separatorChar) +
                                              "phpunit-results/foo.log"
                                              ));
    }

    @Test
    public void setParentThatDoesNotHaveErrorOrFailureChild() throws FileNotFoundException {
        List<TestSuite> testResults = TestResultConverter.convert(new File(System.getProperty("user.dir") +
                                                                           String.valueOf(File.separatorChar) +
                                                                           "phpunit-results/pass.log"
                                                                           ));

        assertEquals(1, testResults.size());

        TestSuite rootSuite = testResults.get(0);
        assertFalse(rootSuite.hasError());
        assertFalse(rootSuite.hasFailure());

        assertEquals(3, rootSuite.getTestResults().size());
        for (TestResult result: rootSuite.getTestResults()) {
            assertFalse(result.hasError());
            assertFalse(result.hasFailure());
        }
    }

    @Test
    public void setParentThatHasFailureChild() throws FileNotFoundException {
        List<TestSuite> testResults = TestResultConverter.convert(new File(System.getProperty("user.dir") +
                                                                           String.valueOf(File.separatorChar) +
                                                                           "phpunit-results/failure.log"
                                                                           ));

        assertEquals(1, testResults.size());

        TestSuite rootSuite = testResults.get(0);
        assertFalse(rootSuite.hasError());
        assertTrue(rootSuite.hasFailure());

        assertEquals(1, rootSuite.getTestResults().size());
        for (TestResult result: rootSuite.getTestResults()) {
            assertFalse(result.hasError());
            assertTrue(result.hasFailure());
        }
    }

    @Test
    public void setParentThatHasErrorChild() throws FileNotFoundException {
        List<TestSuite> testResults = TestResultConverter.convert(new File(System.getProperty("user.dir") +
                                                                           String.valueOf(File.separatorChar) +
                                                                           "phpunit-results/error.log"
                                                                           ));

        assertEquals(1, testResults.size());

        TestSuite rootSuite = testResults.get(0);
        assertTrue(rootSuite.hasError());
        assertFalse(rootSuite.hasFailure());

        assertEquals(1, rootSuite.getTestResults().size());
        for (TestResult result: rootSuite.getTestResults()) {
            assertTrue(result.hasError());
            assertFalse(result.hasFailure());
        }
    }
}
