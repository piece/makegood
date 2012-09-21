<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2009-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2009-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.3.1
 * @link       http://simpletest.org/
 * @since      File available since Release 2.10.0
 */

namespace Stagehand\TestRunner\Runner\SimpleTestRunner;

use Stagehand\TestRunner\JUnitXMLWriter\JUnitXMLWriter;
use Stagehand\TestRunner\TestSuite\SimpleTestTestSuite;
use Stagehand\TestRunner\Util\FailureTrace;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2009-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.3.1
 * @link       http://simpletest.org/
 * @since      Class available since Release 2.10.0
 */
class JUnitXMLReporter extends \SimpleReporter
{
    /**
     * @var \Stagehand\TestRunner\JUnitXMLWriter\JUnitXMLWriter
     */
    protected $junitXMLWriter;

    /**
     * @var \Stagehand\TestRunner\TestSuite\SimpleTestTestSuite
     */
    protected $suite;
    protected $methodStartTime;
    protected $assertionCount;
    protected $reportedFailure;

    /**
     * @var boolean
     * @since Property available since Release 2.17.0
     */
    protected $caseStarted = false;

    /**
     * @var boolean
     * @since Property available since Release 2.17.0
     */
    protected $caseIsArtificial = false;

    /**
     * @var boolean
     * @since Property available since Release 2.17.0
     */
    protected $methodStarted = false;

    /**
     * @param \Stagehand\TestRunner\JUnitXMLWriter\JUnitXMLWriter $junitXMLWriter
     */
    public function setJUnitXMLWriter(JUnitXMLWriter $junitXMLWriter)
    {
        $this->junitXMLWriter = $junitXMLWriter;
    }

    /**
     * @param \Stagehand\TestRunner\TestSuite\SimpleTestTestSuite $suite
     */
    public function setTestSuite(SimpleTestTestSuite $suite)
    {
        $this->suite = $suite;
    }

    /**
     * @param string  $testName
     * @param integer $size
     */
    public function paintGroupStart($testName, $size)
    {
        parent::paintGroupStart($testName, $size);
        if (count($this->getTestList()) == 1) {
            $this->junitXMLWriter->startTestSuites();
        }
        $this->junitXMLWriter->startTestSuite($testName, $this->suite->countTests());
    }

    /**
     * @param string $testName
     */
    public function paintGroupEnd($testName)
    {
        parent::paintGroupEnd($testName);
        $this->junitXMLWriter->endTestSuite();
        if (count($this->getTestList()) == 0) {
            $this->junitXMLWriter->endTestSuites();
        }
    }

    /**
     * @param string $testName
     */
    public function paintCaseStart($testName)
    {
        parent::paintCaseStart($testName);
        $this->junitXMLWriter->startTestSuite(
            $testName,
            $this->suite->countTestsInTestCase(\SimpleTest::getContext()->getTest())
        );
        $this->caseStarted = true;
    }

    /**
     * @param string $testName
     */
    public function paintCaseEnd($testName)
    {
        parent::paintCaseEnd($testName);
        $this->junitXMLWriter->endTestSuite();
        $this->caseStarted = false;
    }

    /**
     * @param string $testName
     */
    public function paintMethodStart($testName)
    {
        $this->caseIsArtificial = false;
        if (!$this->caseStarted) {
            $this->paintCaseStart(\SimpleTest::getContext()->getTest()->getLabel());
            $this->caseIsArtificial = true;
        }

        parent::paintMethodStart($testName);
        $this->junitXMLWriter->startTestCase(
            $testName,
            \SimpleTest::getContext()->getTest()
        );
        $this->methodStartTime = microtime(true);
        $this->assertionCount = 0;
        $this->reportedFailure = false;
        $this->methodStarted = true;
    }

    /**
     * @param string $testName
     */
    public function paintMethodEnd($testName)
    {
        $elapsedTime = microtime(true) - $this->methodStartTime;
        parent::paintMethodEnd($testName);
        $this->junitXMLWriter->endTestCase($elapsedTime, $this->assertionCount);
        $this->methodStarted = false;

        if ($this->caseIsArtificial) {
            $this->paintCaseEnd(\SimpleTest::getContext()->getTest()->getLabel());
        }
    }

    /**
     * @param string $message
     */
    public function paintPass($message)
    {
        parent::paintPass($message);
        ++$this->assertionCount;
    }

    /**
     * @param string $message
     */
    public function paintFail($message)
    {
        if ($this->reportedFailure) return;
        parent::paintFail($message);
        if (preg_match('!^(.*) at \[(.+) line (\d+)]$!', $message, $matches)) {
            $this->writeFailure($matches[1], null, $matches[2], $matches[3], $matches[1]);
        } else {
            $this->writeFailure($message);
        }
        ++$this->assertionCount;
        $this->reportedFailure = true;
    }

    /**
     * @param string $message
     */
    public function paintError($message)
    {
        parent::paintError($message);
        if (preg_match('!^Unexpected PHP error \[(.*)\] severity \[.*\] in \[(.+) line (\d+)]$!', $message, $matches)) {
            $this->writeError($matches[1], null, $matches[2], $matches[3], $matches[1]);
        } else {
            $this->writeError($message);
        }
    }

    /**
     * @param \Exception $e
     */
    public function paintException(\Exception $e)
    {
        parent::paintException($e);
        $this->writeError(
            get_class($e) . ': ' . $e->getMessage() . PHP_EOL . PHP_EOL . $e->getFile() . ':' . $e->getLine() . PHP_EOL . FailureTrace::buildFailureTrace($e->getTrace()),
            null,
            $e->getFile(),
            $e->getLine(),
            $e->getMessage()
        );
    }

    /**
     * @param string $message
     */
    public function paintSkip($message)
    {
        parent::paintSkip($message);
        if (preg_match('!^(.*) at \[(.+) line (\d+)]$!', $message, $matches)) {
            $this->writeError($matches[1], null, $matches[2], $matches[3], $matches[1]);
        } else {
            $this->writeError($message);
        }
    }

    /**
     * @param string $text
     * @param string $type
     * @param string $file
     * @param string $line
     * @param string $message
     * @since Method available since Release 2.17.0
     */
    protected function writeFailure($text, $type = null, $file = null, $line = null, $message = null)
    {
        $this->writeFailureOrError($text, $type, $file, $line, $message, 'failure');
    }

    /**
     * @param string $text
     * @param string $type
     * @param string $file
     * @param string $line
     * @param string $message
     * @since Method available since Release 2.17.0
     */
    protected function writeError($text, $type = null, $file = null, $line = null, $message = null)
    {
        $this->writeFailureOrError($text, $type, $file, $line, $message, 'error');
    }

    /**
     * @param string $text
     * @param string $type
     * @param string $file
     * @param string $line
     * @param string $message
     * @param string $failureOrError
     * @since Method available since Release 2.17.0
     */
    protected function writeFailureOrError($text, $type, $file, $line, $message, $failureOrError)
    {
        $methodIsArtificial = false;
        if (!$this->methodStarted) {
            if (\SimpleTest::getContext()->getTest()->_should_skip) {
                $testName = 'skip';
            } else {
                $testName = 'ARTIFICIAL';
            }
            $this->paintMethodStart($testName);
            $methodIsArtificial = true;
        }

        $this->junitXMLWriter->{ 'write' . $failureOrError }($text, $type, $file, $line, $message);

        if ($methodIsArtificial) {
            $this->paintMethodEnd($testName);
        }
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
