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
 * @version    Release: 3.6.1
 * @link       http://www.phpspec.org/
 * @since      File available since Release 2.1.0
 */

namespace Stagehand\TestRunner\Runner;

use PHPSpec\Runner\ReporterEvent;
use PHPSpec\World;

use Stagehand\TestRunner\Runner\PHPSpecRunner\ExampleFactory;
use Stagehand\TestRunner\Runner\PHPSpecRunner\ExampleRunner;
use Stagehand\TestRunner\Runner\PHPSpecRunner\Formatter\DocumentationFormatter;
use Stagehand\TestRunner\Runner\PHPSpecRunner\Formatter\JUnitXMLFormatter;
use Stagehand\TestRunner\Runner\PHPSpecRunner\Formatter\NotificationFormatter;
use Stagehand\TestRunner\Runner\PHPSpecRunner\Formatter\ProgressFormatter;
use Stagehand\TestRunner\Runner\PHPSpecRunner\Formatter\TerminatableFormatter;
use Stagehand\TestRunner\Runner\PHPSpecRunner\Reporter;
use Stagehand\TestRunner\Runner\PHPSpecRunner\SpecLoaderFactory;

/**
 * A test runner for PHPSpec.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2007-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @link       http://www.phpspec.org/
 * @since      Class available since Release 2.1.0
 */
class PHPSpecRunner extends Runner
{
    /**
     * Runs tests based on the given array.
     *
     * @param \Stagehand\TestRunner\TestSuite\PHPSpecTestSuite $suite
     */
    public function run($suite)
    {
        $options = array();
        $options['specFile'] = $suite;
        $options['c'] = $this->terminal->shouldColor();
        $options['failfast'] = $this->shouldStopOnFailure();

        $reporter = new Reporter();

        if ($this->hasDetailedProgress()) {
            $reporter->addFormatter(new TerminatableFormatter(new DocumentationFormatter($reporter)));
        } else {
            $reporter->addFormatter(new TerminatableFormatter(new ProgressFormatter($reporter)));
        }

        if ($this->shouldNotify()) {
            $notificationFormatter = new NotificationFormatter($reporter);
            $reporter->addFormatter(new TerminatableFormatter($notificationFormatter));
        }

        if ($this->hasJUnitXMLFile()) {
            $junitXMLFormatter = new JUnitXMLFormatter($reporter);
            $junitXMLFormatter->setJUnitXMLWriter($this->createJUnitXMLWriter());
            $junitXMLFormatter->setTestSuite($suite);
            $junitXMLFormatter->setTestTargetRepository($this->testTargetRepository);
            $reporter->addFormatter(new TerminatableFormatter($junitXMLFormatter));
        }

        $world = new World();
        $world->setOptions($options);
        $world->setReporter($reporter);

        $oldErrorHandler = set_error_handler(function () {});
        restore_error_handler();

        $exampleRunner = new ExampleRunner();
        $exampleRunner->setExampleFactory(new ExampleFactory($this->testTargetRepository));
        $runner = new \PHPSpec\Runner\Cli\Runner();
        $runner->setLoader(new SpecLoaderFactory());
        $runner->setExampleRunner($exampleRunner);
        $runner->run($world);
        $reporter->notify(new ReporterEvent('termination', '', ''));

        if (!is_null($oldErrorHandler)) {
            set_error_handler($oldErrorHandler);
        }

        if ($this->shouldNotify()) {
            $this->notification = $notificationFormatter->getNotification();
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
