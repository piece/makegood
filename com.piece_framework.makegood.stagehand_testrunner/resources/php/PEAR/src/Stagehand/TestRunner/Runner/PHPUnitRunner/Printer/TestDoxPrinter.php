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
 * @version    Release: 2.11.1
 * @link       http://www.phpunit.de/
 * @since      File available since Release 2.4.0
 */

require_once 'PHPUnit/Util/TestDox/ResultPrinter/Text.php';
require_once 'PHPUnit/Runner/BaseTestRunner.php';

/**
 * A result printer for TestDox documentation.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2008-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.1
 * @link       http://www.phpunit.de/
 * @since      Class available since Release 2.4.0
 */
class Stagehand_TestRunner_Runner_PHPUnitRunner_Printer_TestDoxPrinter extends PHPUnit_Util_TestDox_ResultPrinter_Text
{

    protected $colors;
    protected $testStatuses = array();
    protected $testStatusMessages = array();
    protected $testTypeOfInterest = 'PHPUnit_Framework_Test';

    /**
     * Constructor.
     *
     * @param  resource $out
     * @param  boolean  $colors
     * @param  mixed    $prettifier
     */
    public function __construct($out = NULL, $colors, $prettifier)
    {
        parent::__construct($out);
        $this->colors = $colors;
        $this->prettifier = $prettifier;
    }

    /**
     * @param PHPUnit_Framework_Test $test
     * @param Exception              $e
     * @param float                  $time
     * @since Method available since Release 2.11.0
     */
    public function addIncompleteTest(PHPUnit_Framework_Test $test, Exception $e, $time)
    {
        parent::addIncompleteTest($test, $e, $time);
        $this->testStatusMessages[ $this->currentTestMethodPrettified ] = $test->getStatusMessage();
    }

    /**
     * Skipped test.
     *
     * @param  PHPUnit_Framework_Test $test
     * @param  Exception              $e
     * @param  float                  $time
     * @since Method available since Release 2.11.0
     */
    public function addSkippedTest(PHPUnit_Framework_Test $test, Exception $e, $time)
    {
        parent::addSkippedTest($test, $e, $time);
        $this->testStatusMessages[ $this->currentTestMethodPrettified ] = $test->getStatusMessage();
    }

    /**
     * @param PHPUnit_Framework_Test $test
     * @since Method available since Release 2.7.0
     */
    public function startTest(PHPUnit_Framework_Test $test)
    {
        if (!$test instanceof PHPUnit_Framework_Warning) {
            parent::startTest($test);
        }
    }

    /**
     * @param PHPUnit_Framework_Test $test
     * @param float                  $time
     * @since Method available since Release 2.7.0
     */
    public function endTest(PHPUnit_Framework_Test $test, $time)
    {
        $this->testStatuses[ $this->currentTestMethodPrettified ] = $this->testStatus;
        parent::endTest($test, $time);
    }

    /**
     * @param string  $name
     * @param boolean $success
     * @since Method available since Release 2.7.0
     */
    protected function onTest($name, $success = true)
    {
        if (!strlen($name)) {
            return;
        }

        $testStatus = $this->testStatuses[$name];
        if ($this->testStatuses[$name] == PHPUnit_Runner_BaseTestRunner::STATUS_INCOMPLETE
            || $this->testStatuses[$name] == PHPUnit_Runner_BaseTestRunner::STATUS_SKIPPED) {
            if (strlen($this->testStatusMessages[$name])) {
                $name = $name .
                        ' (' .
                        str_replace(
                            array("\x0d", "\x0a"),
                            '',
                            $this->testStatusMessages[$name]
                        ) .
                        ')';
            }
        }

        if ($this->colors) {
            switch ($testStatus) {
            case PHPUnit_Runner_BaseTestRunner::STATUS_PASSED:
                $name = Stagehand_TestRunner_Coloring::green($name);
                break;
            case PHPUnit_Runner_BaseTestRunner::STATUS_ERROR:
                $name = Stagehand_TestRunner_Coloring::magenta($name);
                break;
            case PHPUnit_Runner_BaseTestRunner::STATUS_FAILURE:
                $name = Stagehand_TestRunner_Coloring::red($name);
                break;
            case PHPUnit_Runner_BaseTestRunner::STATUS_INCOMPLETE:
            case PHPUnit_Runner_BaseTestRunner::STATUS_SKIPPED:
                $name = Stagehand_TestRunner_Coloring::yellow($name);
                break;
            }
        }

        parent::onTest($name, $success);
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
