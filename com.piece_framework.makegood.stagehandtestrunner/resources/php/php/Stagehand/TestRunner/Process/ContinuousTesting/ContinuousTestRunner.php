<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2011-2013 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2011-2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      File available since Release 2.18.0
 */

namespace Stagehand\TestRunner\Process\ContinuousTesting;

use Symfony\Component\Process\Process;

use Stagehand\TestRunner\Core\TestTargetRepository;
use Stagehand\TestRunner\Notification\Notification;
use Stagehand\TestRunner\Notification\Notifier;
use Stagehand\TestRunner\Preparer\Preparer;
use Stagehand\TestRunner\Process\ContinuousTesting\AlterationMonitoring;
use Stagehand\TestRunner\Process\TestRunnerInterface;
use Stagehand\TestRunner\Runner\Runner;
use Stagehand\TestRunner\Util\LegacyProxy;
use Stagehand\TestRunner\Util\OS;
use Stagehand\TestRunner\Util\String;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      Class available since Release 2.18.0
 */
class ContinuousTestRunner implements TestRunnerInterface
{
    /**
     * @var \Stagehand\TestRunner\Util\OS
     * @since Property available since Release 3.0.1
     */
    protected $os;

    /**
     * @var \Stagehand\TestRunner\Core\TestTargetRepository
     * @since Property available since Release 3.0.0
     */
    protected $testTargetRepository;

    /**
     * @var array
     * @since Property available since Release 3.0.0
     */
    protected $watchDirs;

    /**
     * @var \Stagehand\TestRunner\Preparer\Preparer
     * @since Property available since Release 3.0.1
     */
    protected $preparer;

    /**
     * @var \Stagehand\TestRunner\Runner\Runner
     * @since Property available since Release 3.6.0
     */
    protected $runner;

    /**
     * @var \Stagehand\TestRunner\Notification\Notifier
     * @since Method available since Release 3.6.0
     */
    protected $notifier;

    /**
     * @var \Stagehand\TestRunner\Util\LegacyProxy
     * @since Property available since Release 3.0.0
     */
    protected $legacyProxy;

    /**
     * @var \Stagehand\TestRunner\Process\ContinuousTesting\AlterationMonitoring
     */
    protected $alterationMonitoring;

    /**
     * @var \Stagehand\TestRunner\Process\ContinuousTesting\CommandLineBuilder
     */
    protected $commandLineBuilder;

    /**
     * @param \Stagehand\TestRunner\Preparer\Preparer $preparer
     * @param \Stagehand\TestRunner\Process\ContinuousTesting\CommandLineBuilder $commandLineBuilder
     * @since Method available since Release 3.0.1
     */
    public function __construct(Preparer $preparer, CommandLineBuilder $commandLineBuilder)
    {
        $this->preparer = $preparer;
        $this->commandLineBuilder = $commandLineBuilder;
    }

    /**
     * @since Method available since Release 3.6.0
     */
    public function run()
    {
        $this->preparer->prepare();
        $commandLine = $this->commandLineBuilder->build();
        $this->runTests($commandLine);
        $this->monitorAlteration($commandLine);
    }

    /**
     * @param \Stagehand\TestRunner\Util\OS $os
     * @since Method available since Release 3.0.1
     */
    public function setOS(OS $os)
    {
        $this->os = $os;
    }

    /**
     * Monitors for changes in one or more target directories and runs tests in
     * the test directory recursively when changes are detected. And also the test
     * directory is always added to the directories to be monitored.
     *
     * @param string $commandLine
     */
    public function monitorAlteration($commandLine)
    {
        $self = $this;
        $this->alterationMonitoring->monitor($this->getMonitoringDirectories(), function (array $resourceChangeEvents) use ($self, $commandLine) {
            $self->runTests($commandLine);
        });
    }

    /**
     * @param string $commandLine
     * @since Method available since Release 2.18.0
     */
    public function runTests($commandLine)
    {
        $streamOutput = '';
        if ($this->os->isWin()) {
            // TODO: Remove Windows specific code if the bug #60120 and #51800 are really fixed.
            ob_start(function ($buffer) use (&$streamOutput) {
                $streamOutput .= $buffer;
                return $buffer;
            }, 2
            );
            passthru($commandLine, $exitStatus);
            ob_end_flush();
        } else {
            $process = new Process($commandLine);
            $process->setTimeout(1);
            $exitStatus = $process->run(function ($type, $data) {
                echo $data;
            });
            $streamOutput = $process->getOutput();
        }

        if ($exitStatus != 0 && $this->runner->shouldNotify()) {
            $fatalError = new FatalError($streamOutput);
            $this->notifier->notifyResult(
                new Notification(Notification::RESULT_STOPPED, $fatalError->getFullMessage())
            );
        }
    }

    /**
     * @param \Stagehand\TestRunner\Core\TestTargetRepository $testTargetRepository
     * @since Method available since Release 3.0.0
     */
    public function setTestTargetRepository(TestTargetRepository $testTargetRepository)
    {
        $this->testTargetRepository = $testTargetRepository;
    }

    /**
     * @param array $watchDirs
     * @since Method available since Release 3.0.0
     */
    public function setWatchDirs(array $watchDirs)
    {
        $this->watchDirs = $watchDirs;
    }

    /**
     * @param \Stagehand\TestRunner\Util\LegacyProxy $legacyProxy
     * @since Method available since Release 3.0.0
     */
    public function setLegacyProxy(LegacyProxy $legacyProxy)
    {
        $this->legacyProxy = $legacyProxy;
    }

    /**
     * @param \Stagehand\TestRunner\Process\ContinuousTesting\AlterationMonitoring $alterationMonitoring
     * @since Method available since Release 3.0.0
     */
    public function setAlterationMonitoring(AlterationMonitoring $alterationMonitoring)
    {
        $this->alterationMonitoring = $alterationMonitoring;
    }

    /**
     * @param \Stagehand\TestRunner\Runner\Runner $runner
     * @since Method available since Release 3.6.0
     */
    public function setRunner(Runner $runner)
    {
        $this->runner = $runner;
    }

    /**
     * @param \Stagehand\TestRunner\Notification\Notifier $notifier
     * @since Method available since Release 3.6.0
     */
    public function setNotifier(Notifier $notifier)
    {
        $this->notifier = $notifier;
    }

    /**
     * @return array
     * @throws \UnexpectedValueException
     */
    protected function getMonitoringDirectories()
    {
        $watchDirs = array();
        foreach (array_merge($this->watchDirs,$this->testTargetRepository->getResources()) as $directory) {
            if (!$this->legacyProxy->is_dir($directory)) {
                throw new \UnexpectedValueException(sprintf('A specified path [ %s ] is not found or not a directory.', $directory));
            }

            $directory = $this->legacyProxy->realpath($directory);
            if ($directory === false) {
                throw new \UnexpectedValueException(sprintf('Cannnot get the absolute path of a specified directory [ %s ]. Make sure all elements of the absolute path have valid permissions.', $directory));
            }

            if (!in_array($directory, $watchDirs)) {
                $watchDirs[] = $directory;
            }
        }

        return $watchDirs;
    }

    /**
     * @param string $runnerCommand
     * @return integer
     * @since Method available since Release 2.20.0
     */
    protected function executeRunnerCommand($runnerCommand)
    {
        return $this->legacyProxy->passthru($runnerCommand);
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
