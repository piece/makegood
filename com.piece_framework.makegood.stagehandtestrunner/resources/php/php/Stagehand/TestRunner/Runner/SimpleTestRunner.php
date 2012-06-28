<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2007 Masahiko Sakamoto <msakamoto-sf@users.sourceforge.net>,
 *               2007-2012 KUBO Atsuhiro <kubo@iteman.jp>,
 *               2010 KUMAKURA Yousuke <kumatch@gmail.com>,
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
 * @copyright  2007 Masahiko Sakamoto <msakamoto-sf@users.sourceforge.net>
 * @copyright  2007-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @copyright  2010 KUMAKURA Yousuke <kumatch@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.1.0
 * @link       http://simpletest.org/
 * @since      File available since Release 2.1.0
 */

namespace Stagehand\TestRunner\Runner;

use Stagehand\TestRunner\Runner\SimpleTestRunner\ClassFilterReporter;
use Stagehand\TestRunner\Runner\SimpleTestRunner\JUnitXMLReporterFactory;
use Stagehand\TestRunner\Runner\SimpleTestRunner\MethodFilterReporter;
use Stagehand\TestRunner\Runner\SimpleTestRunner\StopOnFailureReporter;
use Stagehand\TestRunner\Runner\SimpleTestRunner\TextReporter;

/**
 * A test runner for SimpleTest.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2007 Masahiko Sakamoto <msakamoto-sf@users.sourceforge.net>
 * @copyright  2007-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @copyright  2010 KUMAKURA Yousuke <kumatch@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.1.0
 * @link       http://simpletest.org/
 * @since      Class available since Release 2.1.0
 */
class SimpleTestRunner extends Runner
{
    /**
     * @var \Stagehand\TestRunner\Runner\SimpleTestRunner\JUnitXMLReporterFactory
     */
    protected $junitXMLReporterFactory;

    /**
     * Runs tests based on the given TestSuite object.
     *
     * @param \Stagehand\TestRunner\TestSuite\SimpleTestTestSuite $suite
     */
    public function run($suite)
    {
        $textReporter = new TextReporter();
        $textReporter->setRunner($this);
        $textReporter->setTerminal($this->terminal);
        $reporter = new \MultipleReporter();
        $reporter->attachReporter($this->decorateReporter($textReporter));

        if ($this->logsResultsInJUnitXML) {
            $reporter->attachReporter($this->decorateReporter($this->junitXMLReporterFactory->create(
                $this->createStreamWriter($this->junitXMLFile),
                $suite
            )));
        }

        $suite->run($reporter);

        $this->notification = $textReporter->getNotification();
    }

    /**
     * @param \Stagehand\TestRunner\Runner\SimpleTestRunner\JUnitXMLReporterFactory $junitXMLReporterFactory
     */
    public function setJUnitXMLReporterFactory(JUnitXMLReporterFactory $junitXMLReporterFactory)
    {
        $this->junitXMLReporterFactory = $junitXMLReporterFactory;
    }

    /**
     * @param mixed $reporter
     * @return mixed
     * @since Method available since Release 2.10.0
     */
    protected function decorateReporter($reporter)
    {
        $reporters[] = $reporter;

        if ($this->testTargets->testsOnlySpecifiedMethods()) {
            $reporters[] = $this->createMethodFilterReporter($reporters[ count($reporters) - 1 ]);
            $reporters[ count($reporters) - 1 ]->setTestTargets($this->testTargets);
        }

        if ($this->testTargets->testsOnlySpecifiedClasses()) {
            $reporters[] = new ClassFilterReporter($reporters[ count($reporters) - 1 ]);
            $reporters[ count($reporters) - 1 ]->setTestTargets($this->testTargets);
        }

        if ($this->stopsOnFailure) {
            $reporters[] = new StopOnFailureReporter($reporters[ count($reporters) - 1 ]);
        }

        return $reporters[ count($reporters) - 1 ];
    }

    /**
     * @param mixed $reporter
     * @return \SimpleReporterDecorator
     * @since Method available since Release 2.14.2
     */
    protected function createMethodFilterReporter($reporter)
    {
        return new MethodFilterReporter($reporter);
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
