<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2008-2010 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2008-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.2
 * @link       http://www.phpunit.de/
 * @since      File available since Release 1.2.0
 */

require_once 'PHPUnit/TextUI/ResultPrinter.php';
require_once 'PHPUnit/Framework/Test.php';
require_once 'PHPUnit/Framework/AssertionFailedError.php';
require_once 'PHPUnit/Framework/TestSuite.php';

/**
 * A result printer for PHPUnit.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2008-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.2
 * @link       http://www.phpunit.de/
 * @since      Class available since Release 1.2.0
 */
class Stagehand_TestRunner_Runner_PHPUnitRunner_Printer_DetailedProgressPrinter extends PHPUnit_TextUI_ResultPrinter
{
    /**
     * An error occurred.
     *
     * @param  PHPUnit_Framework_Test $test
     * @param  Exception              $e
     * @param  float                  $time
     */
    public function addError(PHPUnit_Framework_Test $test, Exception $e, $time)
    {
        $message = 'raised an error';
        $this->write(
            $this->colors ? Stagehand_TestRunner_Coloring::magenta($message)
                          : $message
        );

        parent::addError($test, $e, $time);
    }

    /**
     * A failure occurred.
     *
     * @param  PHPUnit_Framework_Test                 $test
     * @param  PHPUnit_Framework_AssertionFailedError $e
     * @param  float                                  $time
     */
    public function addFailure(PHPUnit_Framework_Test $test, PHPUnit_Framework_AssertionFailedError $e, $time)
    {
        $message = 'failed';
        $this->write(
            $this->colors ? Stagehand_TestRunner_Coloring::red($message)
                          : $message
        );

        parent::addFailure($test, $e, $time);
    }

    /**
     * Incomplete test.
     *
     * @param  PHPUnit_Framework_Test $test
     * @param  Exception              $e
     * @param  float                  $time
     */
    public function addIncompleteTest(PHPUnit_Framework_Test $test, Exception $e, $time)
    {
        $message = 'was incomplete';
        $message = 'skipped';
        $statusMessage = $test->getStatusMessage();
        if (strlen($statusMessage)) {
            $message .= ' (' . $statusMessage . ')';
        }

        $this->write(
            $this->colors ? Stagehand_TestRunner_Coloring::yellow($message)
                          : $message
        );

        parent::addIncompleteTest($test, $e, $time);
    }

    /**
     * Skipped test.
     *
     * @param  PHPUnit_Framework_Test $test
     * @param  Exception              $e
     * @param  float                  $time
     */
    public function addSkippedTest(PHPUnit_Framework_Test $test, Exception $e, $time)
    {
        $message = 'skipped';
        $statusMessage = $test->getStatusMessage();
        if (strlen($statusMessage)) {
            $message .= ' (' . $statusMessage . ')';
        }

        $this->write(
            $this->colors ? Stagehand_TestRunner_Coloring::yellow($message)
                          : $message
        );

        parent::addSkippedTest($test, $e, $time);
    }

    /**
     * A testsuite started.
     *
     * @param  PHPUnit_Framework_TestSuite $suite
     */
    public function startTestSuite(PHPUnit_Framework_TestSuite $suite)
    {
        if (strlen($suite->getName())) {
            if ($this->lastEvent == PHPUnit_TextUI_ResultPrinter::EVENT_TESTSUITE_END
                || $this->lastEvent == PHPUnit_TextUI_ResultPrinter::EVENT_TEST_END) {
                $this->write("\n\n");
            }

            $this->write($suite->getName() . "\n");
        }

        parent::startTestSuite($suite);
    }

    /**
     * A test started.
     *
     * @param  PHPUnit_Framework_Test $test
     */
    public function startTest(PHPUnit_Framework_Test $test)
    {
        if ($this->lastEvent == PHPUnit_TextUI_ResultPrinter::EVENT_TEST_END
            || $this->lastEvent == PHPUnit_TextUI_ResultPrinter::EVENT_TESTSUITE_END) {
            $this->write("\n");
        }

        $this->write('  ' . $test->getName() . ' ... ');

        parent::startTest($test);
    }

    /**
     * A test ended.
     *
     * @param  PHPUnit_Framework_Test $test
     * @param  float                  $time
     */
    public function endTest(PHPUnit_Framework_Test $test, $time)
    {
        if (!$this->lastTestFailed) {
            $message = 'passed';
            $this->write(
                $this->colors ? Stagehand_TestRunner_Coloring::green($message)
                              : $message
            );
        }

        parent::endTest($test, $time);
    }

    /**
     * @param  string $progress
     * @since  Method available since Release 2.7.0
     */
    protected function writeProgress($progress) {}
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
