<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2011 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.20.0
 * @since      File available since Release 2.18.0
 */

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.20.0
 * @since      Class available since Release 2.18.0
 */
class Stagehand_TestRunner_Autotest
{
    /**
     * @since Constant available since Release 2.20.0
     */
    const FATAL_ERROR_MESSAGE_PATTERN = "/^(?:Parse|Fatal) error: .+ in .+?(?:\(\d+\) : eval\(\)'d code)? on line \d+/m";

    /**
     * @var Stagehand_TestRunner_Config $config
     */
    protected $config;

    /**
     * @var string
     */
    protected $runnerCommand;

    /**
     * @var array
     */
    protected $runnerOptions;

    /**
     * @var string
     */
    protected $output;

    /**
     * @param Stagehand_TestRunner_Config $config
     */
    public function __construct(Stagehand_TestRunner_Config $config)
    {
        $this->config = $config;
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

        $this->createAlterationMonitor()->monitor();
    }

    /**
     * @since Method available since Release 2.18.0
     */
    public function runTests()
    {
        if (is_null($this->runnerCommand)) {
            $this->initializeRunnerCommandAndOptions();
        }

        $this->output = '';
        ob_start(array($this, 'filterOutput'), 2);
        $exitStatus = $this->executeRunnerCommand($this->runnerCommand . ' ' . implode(' ', $this->runnerOptions));
        ob_end_flush();
        if ($exitStatus != 0 && $this->config->usesNotification) {
            $this->createNotifier()->notifyResult(
                new Stagehand_TestRunner_Notification_Notification(
                    Stagehand_TestRunner_Notification_Notification::RESULT_STOPPED,
                    $this->findFatalErrorMessage($this->output)
            ));
        }
    }

    /**
     * @param string $buffer
     * @return string
     */
    public function filterOutput($buffer)
    {
        $this->output .= $buffer;
        return $buffer;
    }

    /**
     * @return array
     * @throws Stagehand_TestRunner_Exception
     */
    protected function getMonitoringDirectories()
    {
        $monitoringDirectories = array();
        foreach (
            array_merge(
                $this->config->monitoringDirectories,
                $this->config->getTestingResources()
            ) as $directory) {
            if (!is_dir($directory)) {
                throw new Stagehand_TestRunner_Exception('A specified path [ ' . $directory . ' ] is not found or not a directory.');
            }

            $directory = realpath($directory);
            if ($directory === false) {
                throw new Stagehand_TestRunner_Exception('Cannnot get the absolute path of a specified directory [ ' . $directory . ' ]. Make sure all elements of the absolute path have valid permissions.');
            }

            if (!in_array($directory, $monitoringDirectories)) {
                $monitoringDirectories[] = $directory;
            }
        }

        return $monitoringDirectories;
    }

    /**
     * @return array
     * @throws Stagehand_TestRunner_Exception
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

        return escapeshellarg($command);
    }

    /**
     * @return array
     */
    protected function buildRunnerOptions()
    {
        $options = array();

        if (!preg_match('!(?:cake|ciunit|phpspec|phpt|phpunit|simpletest)runner$!', trim($this->runnerCommand, '\'"'))) {
            $configFile = $this->getPHPConfigDir();
            if ($configFile !== false) {
                $options[] = '-c';
                $options[] = escapeshellarg($configFile);
            }

            $options[] = escapeshellarg($_SERVER['argv'][0]);
        }

        $options[] = '-R';

        if (!is_null($this->config->preloadFile)) {
            $options[] = '-p ' . escapeshellarg($this->config->preloadFile);
        }

        if ($this->config->colors()) {
            $options[] = '-c';
        }

        if ($this->config->usesNotification) {
            $options[] = '-n';
        }

        if (!is_null($this->config->growlPassword)) {
            $options[] = '--growl-password=' . escapeshellarg($this->config->growlPassword);
        }

        if ($this->config->printsDetailedProgressReport) {
            $options[] = '-v';
        }

        if ($this->config->stopsOnFailure) {
            $options[] = '--stop-on-failure';
        }

        if (!is_null($this->config->phpunitConfigFile)) {
            $options[] = '--phpunit-config=' . escapeshellarg($this->config->phpunitConfigFile);
        }

        if (!is_null($this->config->cakephpAppPath)) {
            $options[] = '--cakephp-app-path=' . escapeshellarg($this->config->cakephpAppPath);
        }

        if (!is_null($this->config->cakephpCorePath)) {
            $options[] = '--cakephp-core-path=' . escapeshellarg($this->config->cakephpCorePath);
        }

        if (!is_null($this->config->ciunitPath)) {
            $options[] = '--ciunit-path=' . escapeshellarg($this->config->ciunitPath);
        }

        if (!is_null($this->config->testFilePattern)) {
            $options[] = '--test-file-pattern=' . escapeshellarg($this->config->testFilePattern);
        }

        if (!is_null($this->config->testFileSuffix)) {
            $options[] = '--test-file-suffix=' . escapeshellarg($this->config->testFileSuffix);
        }

        foreach ($this->config->getTestingResources() as $testingResource) {
            $options[] = escapeshellarg($testingResource);
        }

        return $options;
    }

    /**
     * @return Stagehand_AlterationMonitor
     */
    protected function createAlterationMonitor()
    {
        return new Stagehand_AlterationMonitor($this->getMonitoringDirectories(), array($this, 'runTests'));
    }

    /**
     * @return string
     * @since Method available since Release 2.18.1
     */
    protected function getPHPConfigDir()
    {
        return get_cfg_var('cfg_file_path');
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
        passthru($runnerCommand, $exitStatus);
        return $exitStatus;
    }

    /**
     * @return Stagehand_TestRunner_Notification_Notifier
     * @since Method available since Release 2.20.0
     */
    protected function createNotifier()
    {
        return new Stagehand_TestRunner_Notification_Notifier();
    }

    /**
     * @param string $output
     * @return string
     * @since Method available since Release 2.20.0
     */
    protected function findFatalErrorMessage($output)
    {
        if (preg_match(
            self::FATAL_ERROR_MESSAGE_PATTERN,
            ltrim(Stagehand_TestRunner_Util_String::normalizeNewlines($output)),
            $matches)) {
            return $matches[0];
        } else {
            return $output;
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
