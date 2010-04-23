<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2009-2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2009-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.1
 * @since      File available since Release 2.10.0
 */

require_once 'simpletest/test_case.php';

/**
 * @package    Stagehand_TestRunner
 * @copyright  2009-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.1
 * @since      Class available since Release 2.10.0
 */
class Stagehand_TestRunner_Runner_SimpleTestRunner_JUnitXMLTest extends Stagehand_TestRunner_Runner_TestCase
{
    protected $framework = Stagehand_TestRunner_Framework::SIMPLETEST;

    /**
     * @test
     */
    public function logsTestResultsIntoTheSpecifiedFileInTheJunitXmlFormat()
    {
        $this->collector->collectTestCase('Stagehand_TestRunner_SimpleTestPassTest');
        $this->collector->collectTestCase('Stagehand_TestRunner_SimpleTestFailureTest');
        $this->collector->collectTestCase('Stagehand_TestRunner_SimpleTestErrorTest');
        $this->runTests();
        $this->assertFileExists($this->config->junitXMLFile);

        $junitXML = new DOMDocument();
        $junitXML->load($this->config->junitXMLFile);
        $this->assertTrue($junitXML->relaxNGValidate(dirname(__FILE__) . '/../../../../../data/pear.piece-framework.com/Stagehand_TestRunner/JUnitXMLDOM.rng'));

        $parentTestsuite = $junitXML->childNodes->item(0)->childNodes->item(0);
        $this->assertTrue($parentTestsuite->hasChildNodes());
        $this->assertEquals(5, $parentTestsuite->getAttribute('tests'));
        $this->assertEquals(5, $parentTestsuite->getAttribute('assertions'));
        $this->assertEquals(1, $parentTestsuite->getAttribute('failures'));
        $this->assertEquals(1, $parentTestsuite->getAttribute('errors'));
        $this->assertEquals(3, $parentTestsuite->childNodes->length);

        $childTestsuite = $parentTestsuite->childNodes->item(0);
        $this->assertTrue($childTestsuite->hasChildNodes());
        $this->assertEquals('Stagehand_TestRunner_SimpleTestPassTest',
                            $childTestsuite->hasAttribute('name'));
        $this->assertTrue($childTestsuite->hasAttribute('file'));
        $class = new ReflectionClass('Stagehand_TestRunner_SimpleTestPassTest');
        $this->assertEquals($class->getFileName(), $childTestsuite->getAttribute('file'));
        $this->assertEquals(3, $childTestsuite->getAttribute('tests'));
        $this->assertEquals(4, $childTestsuite->getAttribute('assertions'));
        $this->assertEquals(0, $childTestsuite->getAttribute('failures'));
        $this->assertEquals(0, $childTestsuite->getAttribute('errors'));
        $this->assertEquals(3, $childTestsuite->childNodes->length);

        $testcase = $childTestsuite->childNodes->item(0);
        $this->assertFalse($testcase->hasChildNodes());
        $this->assertEquals('testPassWithAnAssertion', $testcase->hasAttribute('name'));
        $this->assertEquals('Stagehand_TestRunner_SimpleTestPassTest',
                            $testcase->hasAttribute('class'));
        $this->assertEquals($class->getFileName(), $testcase->getAttribute('file'));
        $method = $class->getMethod('testPassWithAnAssertion');
        $this->assertEquals($method->getStartLine(), $testcase->getAttribute('line'));
        $this->assertEquals(1, $testcase->getAttribute('assertions'));

        $testcase = $childTestsuite->childNodes->item(1);
        $this->assertFalse($testcase->hasChildNodes());
        $this->assertEquals('testPassWithMultipleAssertions',
                            $testcase->hasAttribute('name'));
        $this->assertEquals('Stagehand_TestRunner_SimpleTestPassTest',
                            $testcase->hasAttribute('class'));
        $this->assertEquals($class->getFileName(), $testcase->getAttribute('file'));
        $method = $class->getMethod('testPassWithMultipleAssertions');
        $this->assertEquals($method->getStartLine(), $testcase->getAttribute('line'));
        $this->assertEquals(2, $testcase->getAttribute('assertions'));

        $testcase = $childTestsuite->childNodes->item(2);
        $this->assertFalse($testcase->hasChildNodes());
        $this->assertEquals('test日本語を使用できる', $testcase->hasAttribute('name'));
        $this->assertEquals('Stagehand_TestRunner_SimpleTestPassTest',
                            $testcase->hasAttribute('class'));
        $this->assertEquals($class->getFileName(), $testcase->getAttribute('file'));
        $method = $class->getMethod('test日本語を使用できる');
        $this->assertEquals($method->getStartLine(), $testcase->getAttribute('line'));
        $this->assertEquals(1, $testcase->getAttribute('assertions'));

        $childTestsuite = $parentTestsuite->childNodes->item(1);
        $this->assertTrue($childTestsuite->hasChildNodes());
        $this->assertEquals('Stagehand_TestRunner_SimpleTestFailureTest',
                            $childTestsuite->hasAttribute('name'));
        $this->assertTrue($childTestsuite->hasAttribute('file'));
        $class = new ReflectionClass('Stagehand_TestRunner_SimpleTestFailureTest');
        $this->assertEquals($class->getFileName(), $childTestsuite->getAttribute('file'));
        $this->assertEquals(1, $childTestsuite->getAttribute('tests'));
        $this->assertEquals(1, $childTestsuite->getAttribute('assertions'));
        $this->assertEquals(1, $childTestsuite->getAttribute('failures'));
        $this->assertEquals(0, $childTestsuite->getAttribute('errors'));
        $this->assertEquals(1, $childTestsuite->childNodes->length);

        $testcase = $childTestsuite->childNodes->item(0);
        $this->assertTrue($testcase->hasChildNodes());
        $this->assertEquals('testIsFailure', $testcase->hasAttribute('name'));
        $this->assertEquals('Stagehand_TestRunner_SimpleTestFailureTest',
                            $testcase->hasAttribute('class'));
        $this->assertEquals($class->getFileName(), $testcase->getAttribute('file'));
        $method = $class->getMethod('testIsFailure');
        $this->assertEquals($method->getStartLine(), $testcase->getAttribute('line'));
        $this->assertEquals(1, $testcase->getAttribute('assertions'));
        $failure = $testcase->childNodes->item(0);
        $this->assertRegexp('/^This is an error message\./', $failure->nodeValue);

        $childTestsuite = $parentTestsuite->childNodes->item(2);
        $this->assertTrue($childTestsuite->hasChildNodes());
        $this->assertEquals('Stagehand_TestRunner_SimpleTestErrorTest',
                            $childTestsuite->hasAttribute('name'));
        $this->assertTrue($childTestsuite->hasAttribute('file'));
        $class = new ReflectionClass('Stagehand_TestRunner_SimpleTestErrorTest');
        $this->assertEquals($class->getFileName(), $childTestsuite->getAttribute('file'));
        $this->assertEquals(1, $childTestsuite->getAttribute('tests'));
        $this->assertEquals(0, $childTestsuite->getAttribute('assertions'));
        $this->assertEquals(0, $childTestsuite->getAttribute('failures'));
        $this->assertEquals(1, $childTestsuite->getAttribute('errors'));
        $this->assertEquals(1, $childTestsuite->childNodes->length);

        $testcase = $childTestsuite->childNodes->item(0);
        $this->assertTrue($testcase->hasChildNodes());
        $this->assertEquals('testIsError', $testcase->hasAttribute('name'));
        $this->assertEquals('Stagehand_TestRunner_SimpleTestErrorTest',
                            $testcase->hasAttribute('class'));
        $this->assertEquals($class->getFileName(), $testcase->getAttribute('file'));
        $method = $class->getMethod('testIsError');
        $this->assertEquals($method->getStartLine(), $testcase->getAttribute('line'));
        $this->assertEquals(0, $testcase->getAttribute('assertions'));
        $error = $testcase->childNodes->item(0);
        $this->assertRegexp('/^Exception: This is an exception message\./',
                            $error->nodeValue);
    }

    /**
     * @test
     */
    public function logsTestResultsIntoTheSpecifiedFileInTheJunitXmlFormatIfNoTestsAreFound()
    {
        $this->runTests();
        $this->assertFileExists($this->config->junitXMLFile);

        $junitXML = new DOMDocument();
        $junitXML->load($this->config->junitXMLFile);
        $this->assertTrue($junitXML->relaxNGValidate(dirname(__FILE__) . '/../../../../../data/pear.piece-framework.com/Stagehand_TestRunner/JUnitXMLDOM.rng'));

        $parentTestsuite = $junitXML->childNodes->item(0)->childNodes->item(0);
        $this->assertFalse($parentTestsuite->hasChildNodes());
        $this->assertEquals(0, $parentTestsuite->getAttribute('tests'));
        $this->assertEquals(0, $parentTestsuite->getAttribute('assertions'));
        $this->assertEquals(0, $parentTestsuite->getAttribute('failures'));
        $this->assertEquals(0, $parentTestsuite->getAttribute('errors'));
        $this->assertEquals(sprintf('%F', 0), $parentTestsuite->getAttribute('time'));
    }

    /**
     * @test
     */
    public function logsTestResultsInRealtimeIntoTheSpecifiedFileInTheJunitXmlFormat()
    {
        $this->config->logsResultsInJUnitXMLInRealtime = true;
        $this->config->runnerClass = 'Stagehand_TestRunner_Runner_SimpleTestRunner_JUnitXMLTest_MockSimpleTestRunner';
        $this->collector->collectTestCase('Stagehand_TestRunner_SimpleTestPassTest');
        $this->collector->collectTestCase('Stagehand_TestRunner_SimpleTestFailureTest');
        $this->collector->collectTestCase('Stagehand_TestRunner_SimpleTestErrorTest');
        $this->runTests();
        $this->assertFileExists($this->config->junitXMLFile);

        $streamContents = $this->readAttribute($this->runner, 'streamContents');
        $this->assertEquals(22, count($streamContents));
        $this->assertEquals('<?xml version="1.0" encoding="UTF-8"?>
<testsuites', $streamContents[0]);
        $this->assertRegExp('!^><testsuite name="The test suite generated by Stagehand_TestRunner" tests="5"!', $streamContents[1]);
        $this->assertRegExp('!^><testsuite name="Stagehand_TestRunner_SimpleTestPassTest" tests="3" file=".+"!', $streamContents[2]);
        $this->assertRegExp('!^><testcase name="testPassWithAnAssertion" class="Stagehand_TestRunner_SimpleTestPassTest" file=".+" line="\d+"!', $streamContents[3]);
        $this->assertEquals('/>', $streamContents[4]);
        $this->assertRegExp('!^<testcase name="testPassWithMultipleAssertions" class="Stagehand_TestRunner_SimpleTestPassTest" file=".+" line="\d+"!', $streamContents[5]);
        $this->assertEquals('/>', $streamContents[6]);
        $this->assertRegExp('!^<testcase name="test日本語を使用できる" class="Stagehand_TestRunner_SimpleTestPassTest" file=".+" line="\d+"!', $streamContents[7]);
        $this->assertEquals('/>', $streamContents[8]);
        $this->assertEquals('</testsuite>', $streamContents[9]);
        $this->assertEquals('</testsuite></testsuites>
', $streamContents[20]);
        $this->assertEquals('', $streamContents[21]);

        $junitXML = new DOMDocument();
        $junitXML->load($this->config->junitXMLFile);
        $this->assertTrue($junitXML->relaxNGValidate(dirname(__FILE__) . '/../../../../../data/pear.piece-framework.com/Stagehand_TestRunner/JUnitXMLStream.rng'));

        $parentTestsuite = $junitXML->childNodes->item(0)->childNodes->item(0);
        $this->assertTrue($parentTestsuite->hasChildNodes());
        $this->assertEquals(5, $parentTestsuite->getAttribute('tests'));
        $this->assertEquals(3, $parentTestsuite->childNodes->length);

        $childTestsuite = $parentTestsuite->childNodes->item(0);
        $this->assertTrue($childTestsuite->hasChildNodes());
        $this->assertEquals('Stagehand_TestRunner_SimpleTestPassTest',
                            $childTestsuite->hasAttribute('name'));
        $this->assertTrue($childTestsuite->hasAttribute('file'));
        $class = new ReflectionClass('Stagehand_TestRunner_SimpleTestPassTest');
        $this->assertEquals($class->getFileName(), $childTestsuite->getAttribute('file'));
        $this->assertEquals(3, $childTestsuite->getAttribute('tests'));
        $this->assertEquals(3, $childTestsuite->childNodes->length);

        $testcase = $childTestsuite->childNodes->item(0);
        $this->assertFalse($testcase->hasChildNodes());
        $this->assertEquals('testPassWithAnAssertion', $testcase->hasAttribute('name'));
        $this->assertEquals('Stagehand_TestRunner_SimpleTestPassTest',
                            $testcase->hasAttribute('class'));
        $this->assertEquals($class->getFileName(), $testcase->getAttribute('file'));
        $method = $class->getMethod('testPassWithAnAssertion');
        $this->assertEquals($method->getStartLine(), $testcase->getAttribute('line'));

        $testcase = $childTestsuite->childNodes->item(1);
        $this->assertFalse($testcase->hasChildNodes());
        $this->assertEquals('testPassWithMultipleAssertions',
                            $testcase->hasAttribute('name'));
        $this->assertEquals('Stagehand_TestRunner_SimpleTestPassTest',
                            $testcase->hasAttribute('class'));
        $this->assertEquals($class->getFileName(), $testcase->getAttribute('file'));
        $method = $class->getMethod('testPassWithMultipleAssertions');
        $this->assertEquals($method->getStartLine(), $testcase->getAttribute('line'));

        $testcase = $childTestsuite->childNodes->item(2);
        $this->assertFalse($testcase->hasChildNodes());
        $this->assertEquals('test日本語を使用できる', $testcase->hasAttribute('name'));
        $this->assertEquals('Stagehand_TestRunner_SimpleTestPassTest',
                            $testcase->hasAttribute('class'));
        $this->assertEquals($class->getFileName(), $testcase->getAttribute('file'));
        $method = $class->getMethod('test日本語を使用できる');
        $this->assertEquals($method->getStartLine(), $testcase->getAttribute('line'));

        $childTestsuite = $parentTestsuite->childNodes->item(1);
        $this->assertTrue($childTestsuite->hasChildNodes());
        $this->assertEquals('Stagehand_TestRunner_SimpleTestFailureTest',
                            $childTestsuite->hasAttribute('name'));
        $this->assertTrue($childTestsuite->hasAttribute('file'));
        $class = new ReflectionClass('Stagehand_TestRunner_SimpleTestFailureTest');
        $this->assertEquals($class->getFileName(), $childTestsuite->getAttribute('file'));
        $this->assertEquals(1, $childTestsuite->getAttribute('tests'));
        $this->assertEquals(1, $childTestsuite->childNodes->length);

        $testcase = $childTestsuite->childNodes->item(0);
        $this->assertTrue($testcase->hasChildNodes());
        $this->assertEquals('testIsFailure', $testcase->hasAttribute('name'));
        $this->assertEquals('Stagehand_TestRunner_SimpleTestFailureTest',
                            $testcase->hasAttribute('class'));
        $this->assertEquals($class->getFileName(), $testcase->getAttribute('file'));
        $method = $class->getMethod('testIsFailure');
        $this->assertEquals($method->getStartLine(), $testcase->getAttribute('line'));
        $failure = $testcase->childNodes->item(0);
        $this->assertRegexp('/^This is an error message\./', $failure->nodeValue);

        $childTestsuite = $parentTestsuite->childNodes->item(2);
        $this->assertTrue($childTestsuite->hasChildNodes());
        $this->assertEquals('Stagehand_TestRunner_SimpleTestErrorTest',
                            $childTestsuite->hasAttribute('name'));
        $this->assertTrue($childTestsuite->hasAttribute('file'));
        $class = new ReflectionClass('Stagehand_TestRunner_SimpleTestErrorTest');
        $this->assertEquals($class->getFileName(), $childTestsuite->getAttribute('file'));
        $this->assertEquals(1, $childTestsuite->getAttribute('tests'));
        $this->assertEquals(1, $childTestsuite->childNodes->length);

        $testcase = $childTestsuite->childNodes->item(0);
        $this->assertTrue($testcase->hasChildNodes());
        $this->assertEquals('testIsError', $testcase->hasAttribute('name'));
        $this->assertEquals('Stagehand_TestRunner_SimpleTestErrorTest',
                            $testcase->hasAttribute('class'));
        $this->assertEquals($class->getFileName(), $testcase->getAttribute('file'));
        $method = $class->getMethod('testIsError');
        $this->assertEquals($method->getStartLine(), $testcase->getAttribute('line'));
        $error = $testcase->childNodes->item(0);
        $this->assertRegexp('/^Exception: This is an exception message\./',
                            $error->nodeValue);
    }

    /**
     * @test
     * @since Method available since Release 2.11.1
     */
    public function countsTheNumberOfTestsForMethodFiltersWithTheRealtimeOption()
    {
        $this->config->logsResultsInJUnitXMLInRealtime = true;
        $this->config->addMethodToBeTested('testPass1');
        class_exists('Stagehand_TestRunner_SimpleTestMultipleClassesTest');
        $this->collector->collectTestCase('Stagehand_TestRunner_SimpleTestMultipleClasses1Test');
        $this->runTests();
        $junitXML = new DOMDocument();
        $junitXML->load($this->config->junitXMLFile);

        $parentTestsuite = $junitXML->childNodes->item(0)->childNodes->item(0);
        $this->assertEquals(1, $parentTestsuite->getAttribute('tests'));
        $childTestsuite = $parentTestsuite->childNodes->item(0);
        $this->assertEquals(1, $childTestsuite->getAttribute('tests'));
    }

    /**
     * @test
     * @since Method available since Release 2.11.1
     */
    public function countsTheNumberOfTestsForClassFiltersWithTheRealtimeOption()
    {
        $this->config->logsResultsInJUnitXMLInRealtime = true;
        $this->config->addClassToBeTested('Stagehand_TestRunner_SimpleTestMultipleClasses1Test');
        class_exists('Stagehand_TestRunner_SimpleTestMultipleClassesTest');
        $this->collector->collectTestCase('Stagehand_TestRunner_SimpleTestMultipleClasses1Test');
        $this->collector->collectTestCase('Stagehand_TestRunner_SimpleTestMultipleClasses2Test');
        $this->runTests();
        $junitXML = new DOMDocument();
        $junitXML->load($this->config->junitXMLFile);

        $parentTestsuite = $junitXML->childNodes->item(0)->childNodes->item(0);
        $this->assertEquals(2, $parentTestsuite->getAttribute('tests'));
        $childTestsuite = $parentTestsuite->childNodes->item(0);
        $this->assertEquals(2, $childTestsuite->getAttribute('tests'));
    }
}

/*
 * Local Variables:
 * mode: php
 * coding: utf-8
 * tab-width: 4
 * c-basic-offset: 4
 * c-hanging-comment-ender-p: nil
 * indent-tabs-mode: nil
 * End:
 */
