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

/**
 * @package    Stagehand_TestRunner
 * @copyright  2009-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.1
 * @since      Class available since Release 2.10.0
 */
class Stagehand_TestRunner_JUnitXMLWriter_JUnitXMLDOMWriter implements Stagehand_TestRunner_JUnitXMLWriter
{
    protected $xmlWriter;
    protected $streamWriter;
    protected $elementStack = array();

    /**
     * @var Stagehand_TestRunner_JUnitXMLWriter_UTF8Converter
     */
    protected $utf8Converter;

    /**
     * @param callback $streamWriter
     */
    public function __construct($streamWriter)
    { 
        $this->streamWriter = $streamWriter;
        $this->xmlWriter = new DOMDocument('1.0', 'UTF-8');
        $this->utf8Converter = Stagehand_TestRunner_JUnitXMLWriter_UTF8Converter_UTF8ConverterFactory::create();
    }

    /**
     */
    public function startTestSuites()
    {
        $testsuites = $this->xmlWriter->createElement('testsuites');
        $this->xmlWriter->appendChild($testsuites);
        $this->elementStack[] = $testsuites;
    }

    /**
     * @param string  $name
     * @param integer $testCount
     */
    public function startTestSuite($name, $testCount = null)
    {
        if (preg_match('/^(.+)::(.+)/', $name, $matches)) {
            $name = $matches[2];
            $className = $matches[1];
        } else {
            $className = $name;
        }

        $testsuite =
            new Stagehand_TestRunner_JUnitXMLWriter_JUnitXMLDOMWriter_TestsuiteDOMElement();
        $this->getCurrentTestsuite()->appendChild($testsuite);
        $testsuite->setAttribute('name', $this->utf8Converter->convert($name));
        $testsuite->setAttribute('tests', 0);
        $testsuite->setAttribute('assertions', 0);
        $testsuite->setAttribute('failures', 0);
        $testsuite->setAttribute('errors', 0);
        $testsuite->setAttribute('time', sprintf('%F', 0));

        if (strlen($className) && class_exists($className, false)) {
            try {
                $class = new ReflectionClass($className);
                $testsuite->setAttribute('file', $this->utf8Converter->convert($class->getFileName()));
            } catch (ReflectionException $e) {
            }
        }

        $this->elementStack[] = $testsuite;
    }

    /**
     * @param string $name
     * @param mixed  $test
     * @param string $methodName
     */
    public function startTestCase($name, $test, $methodName = null)
    {
        $testcase = $this->xmlWriter->createElement('testcase');
        $this->getCurrentTestsuite()->appendChild($testcase);
        $testcase->setAttribute('name', $this->utf8Converter->convert($name));

        $class = new ReflectionClass($test);
        if (is_null($methodName)) {
            $methodName = $name;
        }
        if ($class->hasMethod($methodName)) {
            $method = $class->getMethod($methodName);

            $testcase->setAttribute('class', $this->utf8Converter->convert($class->getName()));
            $testcase->setAttribute('file', $this->utf8Converter->convert($class->getFileName()));
            $testcase->setAttribute('line', $method->getStartLine());
        }

        $this->elementStack[] = $testcase;
    }

    /**
     * @param string $text
     * @param string $type
     */
    public function writeError($text, $type = null)
    {
        $this->writeFailureOrError($text, $type, 'error');
    }

    /**
     * @param string $text
     * @param string $type
     */
    public function writeFailure($text, $type = null)
    {
        $this->writeFailureOrError($text, $type, 'failure');
    }

    /**
     * @param float   $time
     * @param integer $assertionCount
     */
    public function endTestCase($time, $assertionCount = null)
    {
        $testCase = array_pop($this->elementStack);
        if (!is_null($assertionCount)) {
            $testCase->setAttribute('assertions', $assertionCount);
        }
        $testCase->setAttribute('time', $time);
        $this->getCurrentTestsuite()->addAssertionCount($assertionCount);
        $this->getCurrentTestsuite()->addTime($time);
        $this->getCurrentTestsuite()->increaseTestCount();
    }

    /**
     */
    public function endTestSuite()
    {
        $suite = array_pop($this->elementStack);
        if ($this->getCurrentTestsuite() instanceof
            Stagehand_TestRunner_JUnitXMLWriter_JUnitXMLDOMWriter_TestsuiteDOMElement) {
            $this->getCurrentTestsuite()->addTestCount($suite->getAttribute('tests'));
            if ($suite->hasAttribute('assertions')) {
                $this->getCurrentTestsuite()->addAssertionCount(
                    $suite->getAttribute('assertions')
                );
            }
            $this->getCurrentTestsuite()->addErrorCount($suite->getAttribute('errors'));
            $this->getCurrentTestsuite()->addFailureCount($suite->getAttribute('failures'));
            $this->getCurrentTestsuite()->addTime($suite->getAttribute('time'));
        }
    }

    /**
     */
    public function endTestSuites()
    {
        $this->flush();
    }

    /**
     * @param string $text
     * @param string $type
     * @param string $failureOrError
     */
    protected function writeFailureOrError($text, $type, $failureOrError)
    {
        $error = $this->xmlWriter->createElement($failureOrError, $this->utf8Converter->convert($text));
        $this->getCurrentElement()->appendChild($error);
        if (!is_null($type)) {
            $error->setAttribute('type', $this->utf8Converter->convert($type));
        }

        $this->getCurrentTestsuite()->{ 'increase' . $failureOrError . 'Count' }();
    }

    protected function flush()
    {
        call_user_func($this->streamWriter, $this->xmlWriter->saveXML());
    }

    /**
     * @return Stagehand_TestRunner_JUnitXMLWriter_JUnitXMLDOMWriter_TestsuiteDOMElement
     */
    protected function getCurrentTestsuite()
    {
        if ($this->getCurrentElement()->tagName == 'testcase') {
            return $this->getPreviousElement();
        }

        return $this->getCurrentElement();
    }

    /**
     * @return DOMElement
     */
    protected function getCurrentElement()
    {
        return $this->elementStack[ count($this->elementStack) - 1 ];
    }

    /**
     * @return DOMElement
     */
    protected function getPreviousElement()
    {
        return $this->elementStack[ count($this->elementStack) - 2 ];
    }
}

/*
 * Local Variables:
 * mode: php
 * coding: iso-8859-1
 * tab-width: 4
 * c-basic-offset: 4
 * c-hanging-comment-ender-p: nil
 * indent-tabs-mode: nil
 * End:
 */
