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
 * @version    Release: 3.6.1
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
 * @version    Release: 3.6.1
 * @link       http://www.phpunit.de/
 * @since      Class available since Release 1.2.0
 */
class ProgressPrinter extends ResultPrinter
{
    /**
     * An error occurred.
     *
     * @param  \PHPUnit_Framework_Test $test
     * @param  \Exception              $e
     * @param  float                  $time
     */
    public function addError(\PHPUnit_Framework_Test $test, \Exception $e, $time)
    {
        if (!$this->colors) {
            parent::addError($test, $e, $time);
            return;
        }

        $this->writeProgress(Coloring::magenta('E'));
        $this->lastTestFailed = TRUE;
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
        if (!$this->colors) {
            parent::addFailure($test, $e, $time);
            return;
        }

        $this->writeProgress(Coloring::red('F'));
        $this->lastTestFailed = TRUE;
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
        if (!$this->colors) {
            parent::addIncompleteTest($test, $e, $time);
            return;
        }

        $this->writeProgress(Coloring::yellow('I'));
        $this->lastTestFailed = TRUE;
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
        if (!$this->colors) {
            parent::addSkippedTest($test, $e, $time);
            return;
        }

        $this->writeProgress(Coloring::yellow('S'));
        $this->lastTestFailed = TRUE;
    }

    /**
     * A test ended.
     *
     * @param  \PHPUnit_Framework_Test $test
     * @param  float                  $time
     */
    public function endTest(\PHPUnit_Framework_Test $test, $time)
    {
        if ($this->colors && !$this->lastTestFailed) {
            $this->writeProgress(Coloring::green('.'));
            $this->lastTestFailed = true;
        }

        parent::endTest($test, $time);
        $this->lastEvent = self::EVENT_TEST_END;
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
