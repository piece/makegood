<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2008-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2008-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.5.0
 * @link       http://www.phpunit.de/
 * @since      File available since Release 1.2.0
 */

namespace Stagehand\TestRunner\Runner\PHPUnitRunner\Printer;

use Stagehand\TestRunner\Util\Coloring;

/**
 * A result printer for PHPUnit.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2008-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.5.0
 * @link       http://www.phpunit.de/
 * @since      Class available since Release 1.2.0
 */
class DetailedProgressPrinter extends ResultPrinter
{
    /**
     * @var integer
     * @since Property available since Release 2.16.0
     */
    protected $lastEvent = -1;

    /**
     * An error occurred.
     *
     * @param  \PHPUnit_Framework_Test $test
     * @param  \Exception              $e
     * @param  float                  $time
     */
    public function addError(\PHPUnit_Framework_Test $test, \Exception $e, $time)
    {
        $message = 'raised an error';
        $this->write(
            $this->colors ? Coloring::magenta($message)
                          : $message
        );

        parent::addError($test, $e, $time);
    }

    /**
     * A failure occurred.
     *
     * @param  \PHPUnit_Framework_Test                 $test
     * @param  \PHPUnit_Framework_AssertionFailedError $e
     * @param  float                                  $time
     */
    public function addFailure(\PHPUnit_Framework_Test $test, \PHPUnit_Framework_AssertionFailedError $e, $time)
    {
        $message = 'failed';
        $this->write(
            $this->colors ? Coloring::red($message)
                          : $message
        );

        parent::addFailure($test, $e, $time);
    }

    /**
     * Incomplete test.
     *
     * @param  \PHPUnit_Framework_Test $test
     * @param  \Exception              $e
     * @param  float                  $time
     */
    public function addIncompleteTest(\PHPUnit_Framework_Test $test, \Exception $e, $time)
    {
        $message = 'was incomplete';
        $message = 'skipped';
        $statusMessage = $test->getStatusMessage();
        if (strlen($statusMessage)) {
            $message .= ' (' . $statusMessage . ')';
        }

        $this->write(
            $this->colors ? Coloring::yellow($message)
                          : $message
        );

        parent::addIncompleteTest($test, $e, $time);
    }

    /**
     * Skipped test.
     *
     * @param  \PHPUnit_Framework_Test $test
     * @param  \Exception              $e
     * @param  float                  $time
     */
    public function addSkippedTest(\PHPUnit_Framework_Test $test, \Exception $e, $time)
    {
        $message = 'skipped';
        $statusMessage = $test->getStatusMessage();
        if (strlen($statusMessage)) {
            $message .= ' (' . $statusMessage . ')';
        }

        $this->write(
            $this->colors ? Coloring::yellow($message)
                          : $message
        );

        parent::addSkippedTest($test, $e, $time);
    }

    /**
     * A testsuite started.
     *
     * @param  \PHPUnit_Framework_TestSuite $suite
     */
    public function startTestSuite(\PHPUnit_Framework_TestSuite $suite)
    {
        if (strlen($suite->getName())) {
            if ($this->lastEvent == \PHPUnit_TextUI_ResultPrinter::EVENT_TESTSUITE_END
                || $this->lastEvent == \PHPUnit_TextUI_ResultPrinter::EVENT_TEST_END) {
                $this->write(PHP_EOL . PHP_EOL);
            }

            $this->write($suite->getName() . PHP_EOL);
        }

        parent::startTestSuite($suite);
        $this->lastEvent = \PHPUnit_TextUI_ResultPrinter::EVENT_TESTSUITE_START;
    }

    /**
     * A testsuite ended.
     *
     * @param  \PHPUnit_Framework_TestSuite $suite
     * @since  Method available since Release 2.16.0
     */
    public function endTestSuite(\PHPUnit_Framework_TestSuite $suite)
    {
        parent::startTestSuite($suite);
        $this->lastEvent = \PHPUnit_TextUI_ResultPrinter::EVENT_TESTSUITE_END;
    }

    /**
     * A test started.
     *
     * @param  \PHPUnit_Framework_Test $test
     */
    public function startTest(\PHPUnit_Framework_Test $test)
    {
        if ($this->lastEvent == \PHPUnit_TextUI_ResultPrinter::EVENT_TEST_END
            || $this->lastEvent == \PHPUnit_TextUI_ResultPrinter::EVENT_TESTSUITE_END) {
            $this->write(PHP_EOL);
        }

        $this->write('  ' . $test->getName() . ' ... ');

        $this->lastEvent = \PHPUnit_TextUI_ResultPrinter::EVENT_TEST_START;
        parent::startTest($test);
    }

    /**
     * A test ended.
     *
     * @param  \PHPUnit_Framework_Test $test
     * @param  float                  $time
     */
    public function endTest(\PHPUnit_Framework_Test $test, $time)
    {
        if (!$this->lastTestFailed) {
            $message = 'passed';
            $this->write(
                $this->colors ? Coloring::green($message)
                              : $message
            );
        }

        parent::endTest($test, $time);
        $this->lastEvent = \PHPUnit_TextUI_ResultPrinter::EVENT_TEST_END;
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
