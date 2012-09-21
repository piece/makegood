<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2011-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.3.1
 * @since      File available since Release 2.18.0
 */

namespace Stagehand\TestRunner\Process\Autotest;

use Symfony\Component\Process\Process;

use Stagehand\ComponentFactory\IComponentAwareFactory;
use Stagehand\TestRunner\CLI\Terminal;
use Stagehand\TestRunner\Core\ApplicationContext;
use Stagehand\TestRunner\Core\TestTargetRepository;
use Stagehand\TestRunner\Notification\Notification;
use Stagehand\TestRunner\Process\AlterationMonitoring;
use Stagehand\TestRunner\Process\FatalError;
use Stagehand\TestRunner\Util\LegacyProxy;
use Stagehand\TestRunner\Util\OS;
use Stagehand\TestRunner\Util\String;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.3.1
 * @since      Class available since Release 2.18.0
 */
abstract class Autotest
{
    /**
     * @var \Stagehand\TestRunner\Util\OS
     * @since Property available since Release 3.0.1
     */
    protected $os;

    /**
     * @var string
     */
    protected $runnerCommand;

    /**
     * @var array
     */
    protected $runnerOptions;

    /**
     * @var \Stagehand\TestRunner\CLI\Terminal
     * @since Property available since Release 3.0.0
     */
    protected $terminal;

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
     * @var \Stagehand\ComponentFactory\IComponentAwareFactory
     * @since Property available since Release 3.0.0
     */
    protected $runnerFactory;

    /**
     * @var \Stagehand\ComponentFactory\IComponentAwareFactory
     * @since Method available since Release 3.0.0
     */
    protected $notifierFactory;

    /**
     * @var \Stagehand\TestRunner\Util\LegacyProxy
     * @since Property available since Release 3.0.0
     */
    protected $legacyProxy;

    /**
     * @var \Stagehand\TestRunner\Process\AlterationMonitoring
     */
    protected $alterationMonitoring;

    /**
     * @param \Stagehand\ComponentFactory\IComponentAwareFactory $preparerFactory
     * @since Method available since Release 3.0.1
     */
    public function __construct(IComponentAwareFactory $preparerFactory)
    {
        $this->preparer = $preparerFactory->create();
        $this->preparer->prepare();
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
     */
    public function monitorAlteration()
    {
        if (is_null($this->runnerCommand)) {
            $this->initializeRunnerCommandAndOptions();
        }

        $this->alterationMonitoring->monitor($this->getMonitoringDirectories(), array($this, 'runTests'));
    }

    /**
     * @since Method available since Release 2.18.0
     */
    public function runTests()
    {
        if (is_null($this->runnerCommand)) {
            $this->initializeRunnerCommandAndOptions();
        }

        $streamOutput = '';
        if ($this->os->isWin()) {
            // TODO: Remove Windows specific code if the bug #60120 and #51800 are really fixed.
            ob_start(function ($buffer) use (&$streamOutput) {
                $streamOutput .= $buffer;
                return $buffer;
            }, 2
            );
            passthru($this->runnerCommand . ' ' . implode(' ', $this->runnerOptions), $exitStatus);
            ob_end_flush();
        } else {
            $process = new Process($this->runnerCommand . ' ' . implode(' ', $this->runnerOptions));
            $process->setTimeout(1);
            $exitStatus = $process->run(function ($type, $data) {
                echo $data;
            });
            $streamOutput = $process->getOutput();
        }

        if ($exitStatus != 0 && $this->runnerFactory->create()->shouldNotify()) {
            $fatalError = new FatalError($streamOutput);
            $this->notifierFactory->create()->notifyResult(
                new Notification(Notification::RESULT_STOPPED, $fatalError->getFullMessage())
            );
        }
    }

    /**
     * @param \Stagehand\TestRunner\CLI\Terminal $terminal
     * @since Method available since Release 3.0.0
     */
    public function setTerminal(Terminal $terminal)
    {
        $this->terminal = $terminal;
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
     * @param \Stagehand\TestRunner\Process\AlterationMonitoring $alterationMonitoring
     * @since Method available since Release 3.0.0
     */
    public function setAlterationMonitoring(AlterationMonitoring $alterationMonitoring)
    {
        $this->alterationMonitoring = $alterationMonitoring;
    }

    /**
     * @param \Stagehand\ComponentFactory\IComponentAwareFactory $runnerFactory
     * @since Method available since Release 3.0.0
     */
    public function setRunnerFactory(IComponentAwareFactory $runnerFactory)
    {
        $this->runnerFactory = $runnerFactory;
    }

    /**
     * @param \Stagehand\ComponentFactory\IComponentAwareFactory $notifierFactory
     * @since Method available since Release 3.0.0
     */
    public function setNotifierFactory(IComponentAwareFactory $notifierFactory)
    {
        $this->notifierFactory = $notifierFactory;
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
     * @return array
     */
    protected function buildRunnerCommand()
    {
        if (array_key_exists('_', $_SERVER)) {
            $command = $_SERVER['_'];
        } elseif (array_key_exists('PHP_COMMAND', $_SERVER)) {
            $command = $_SERVER['PHP_COMMAND'];
        } else {
            $command = $_SERVER['argv'][0];
        }

        if (preg_match('!^/cygdrive/([a-z])/(.+)!', $command, $matches)) {
            $command = $matches[1] . ':\\' . str_replace('/', '\\', $matches[2]);
        }

        if ($this->os->isWin()) {
            putenv(sprintf('ENVPATH="%s"', $command));
            return '%ENVPATH%';
        } else {
            return escapeshellarg($command);
        }
    }

    /**
     * @return array
     */
    protected function buildRunnerOptions()
    {
        $options = array();

        if (basename(trim($this->runnerCommand, '\'"')) != 'testrunner') {
            $configFile = $this->getPHPConfigDir();
            if ($configFile !== false) {
                $options[] = '-c';
                $options[] = escapeshellarg($configFile);
            }

            $options[] = escapeshellarg($_SERVER['argv'][0]);
        }

        if ($this->terminal->shouldColor()) {
            $options[] = '--ansi';
        }

        $options[] = escapeshellarg(strtolower(ApplicationContext::getInstance()->getPlugin()->getPluginID()));

        if (!is_null(ApplicationContext::getInstance()->getEnvironment()->getPreloadScript())) {
            $options[] = '-p ' . escapeshellarg(ApplicationContext::getInstance()->getEnvironment()->getPreloadScript());
        }

        $options[] = '-R';

        if ($this->runnerFactory->create()->shouldNotify()) {
            $options[] = '-m';
        }

        if ($this->runnerFactory->create()->shouldStopOnFailure()) {
            $options[] = '--stop-on-failure';
        }

        if (!$this->testTargetRepository->isDefaultFilePattern()) {
            $options[] = '--test-file-pattern=' . escapeshellarg($this->testTargetRepository->getFilePattern());
        }

        if ($this->runnerFactory->create()->hasDetailedProgress()) {
            $options[] = '--detailed-progress';
        }

        $options = array_merge($options, $this->doBuildRunnerOptions());

        $this->testTargetRepository->walkOnResources(function ($resource, $index, TestTargetRepository $testTargetRepository) use (&$options) {
            $options[] = escapeshellarg($resource);
        });

        return $options;
    }

    /**
     * @return string
     * @since Method available since Release 2.18.1
     */
    protected function getPHPConfigDir()
    {
        return $this->legacyProxy->get_cfg_var('cfg_file_path');
    }

    /**
     * @since Method available since Release 2.18.1
     */
    protected function initializeRunnerCommandAndOptions()
    {
        $this->runnerCommand = $this->buildRunnerCommand();
        $this->runnerOptions = $this->buildRunnerOptions();
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

    /**
     * @return array
     * @since Method available since Release 3.0.0
     */
    abstract protected function doBuildRunnerOptions();
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
