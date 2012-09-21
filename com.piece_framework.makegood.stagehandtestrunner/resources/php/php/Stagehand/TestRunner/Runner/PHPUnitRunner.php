<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2007-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2007-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.3.1
 * @link       http://www.phpunit.de/
 * @since      File available since Release 2.1.0
 */

namespace Stagehand\TestRunner\Runner;

use Stagehand\TestRunner\Runner\PHPUnitRunner\Printer\DetailedProgressPrinter;
use Stagehand\TestRunner\Runner\PHPUnitRunner\Printer\JUnitXMLPrinter;
use Stagehand\TestRunner\Runner\PHPUnitRunner\Printer\JUnitXMLPrinterFactory;
use Stagehand\TestRunner\Runner\PHPUnitRunner\Printer\ProgressPrinter;
use Stagehand\TestRunner\Runner\PHPUnitRunner\Printer\ResultPrinter;
use Stagehand\TestRunner\Runner\PHPUnitRunner\Printer\TestDoxPrinter;
use Stagehand\TestRunner\Runner\PHPUnitRunner\TestDox\NamePrettifier;
use Stagehand\TestRunner\Runner\PHPUnitRunner\TestDox\Stream;
use Stagehand\TestRunner\Runner\PHPUnitRunner\TestRunner;
use Stagehand\TestRunner\Util\PHPUnitXMLConfiguration;

/**
 * A test runner for PHPUnit.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2007-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.3.1
 * @link       http://www.phpunit.de/
 * @since      Class available since Release 2.1.0
 */
class PHPUnitRunner extends Runner
{
    /**
     * @var \Stagehand\TestRunner\Util\PHPUnitXMLConfiguration
     * @since Property available since Release 3.0.0
     */
    protected $phpunitXMLConfiguration;

    /**
     * Runs tests based on the given \PHPUnit_Framework_TestSuite object.
     *
     * @param \PHPUnit_Framework_TestSuite $suite
     */
    public function run($suite)
    {
        $printer = $this->createPrinter();
        $testResult = new \PHPUnit_Framework_TestResult();
        $testRunner = new TestRunner();
        $testRunner->setTestResult($testResult);
        $testRunner->doRun($suite, $this->createArguments($printer, $testResult));

        $this->notification = $printer->getNotification();
    }

    /**
     * @param \Stagehand\TestRunner\Util\PHPUnitXMLConfiguration $phpunitXMLConfiguration
     * @since Method available since Release 3.0.0
     */
    public function setPHPUnitXMLConfiguration(PHPUnitXMLConfiguration $phpunitXMLConfiguration = null)
    {
        $this->phpunitXMLConfiguration = $phpunitXMLConfiguration;
    }

    /**
     * @return \PHPUnit_Util_TestDox_NamePrettifier
     * @since Method available since Release 2.7.0
     */
    protected function prettifier()
    {
        return new \PHPUnit_Util_TestDox_NamePrettifier();
    }

    /**
     * @return \Stagehand\TestRunner\Runner\PHPUnitRunner\Printer\ResultPrinter
     * @since Method available since Release 3.3.0
     */
    protected function createPrinter()
    {
        if ($this->hasDetailedProgress()) {
            $printer = new DetailedProgressPrinter(null, false, $this->terminal->shouldColor());
        } else {
            $printer = new ProgressPrinter(null, false, $this->terminal->shouldColor());
        }
        $printer->setRunner($this);

        return $printer;
    }

    /**
     * @param \Stagehand\TestRunner\Runner\PHPUnitRunner\Printer\ResultPrinter $printer
     * @param \PHPUnit_Framework_TestResult $testResult
     * @return array
     * @since Method available since Release 3.3.0
     */
    protected function createArguments(ResultPrinter $printer, \PHPUnit_Framework_TestResult $testResult)
    {
        $arguments = array();
        $arguments['printer'] = $printer;

        Stream::register();
        $arguments['listeners'] =
            array(
                new TestDoxPrinter(
                    fopen('testdox://' . spl_object_hash($testResult), 'w'),
                    $this->terminal,
                    $this->prettifier()
                )
            );

        if ($this->hasJUnitXMLFile()) {
            $arguments['listeners'][] = new JUnitXMLPrinter(
                null,
                $this->createJUnitXMLWriter(),
                $this->testTargetRepository
            );
        }

        if ($this->shouldStopOnFailure()) {
            $arguments['stopOnFailure'] = true;
            $arguments['stopOnError'] = true;
        }

        if ($this->phpunitXMLConfiguration->isEnabled()) {
            $arguments['configuration'] = $this->phpunitXMLConfiguration->getFileName();
        }

        return $arguments;
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
