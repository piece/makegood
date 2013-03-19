<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2013 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.0
 * @since      File available since Release 3.6.0
 */

namespace Stagehand\TestRunner\Process\ContinuousTesting;

use Stagehand\TestRunner\CLI\Terminal;
use Stagehand\TestRunner\Core\Environment;
use Stagehand\TestRunner\Core\Plugin\PluginInterface;
use Stagehand\TestRunner\Runner\Runner;
use Stagehand\TestRunner\Util\LegacyProxy;
use Stagehand\TestRunner\Util\OS;
use Stagehand\TestRunner\Core\TestTargetRepository;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.0
 * @since      Class available since Release 3.6.0
 */
class CommandLineBuilder
{
    /**
     * @var \Stagehand\TestRunner\Core\Environment
     */
    protected $environment;

    /**
     * @var \Stagehand\TestRunner\Util\LegacyProxy
     */
    protected $legacyProxy;

    /**
     * @var \Stagehand\TestRunner\Util\OS
     */
    protected $os;

    /**
     * @var \Stagehand\TestRunner\Core\Plugin\PluginInterface
     */
    protected $plugin;

    /**
     * @var \Stagehand\TestRunner\Runner\Runner
     */
    protected $runner;

    /**
     * @var \Stagehand\TestRunner\CLI\Terminal
     */
    protected $terminal;

    /**
     * @var \Stagehand\TestRunner\Core\TestTargetRepository
     */
    protected $testTargetRepository;

    /**
     * @var \Stagehand\TestRunner\Process\ContinuousTesting\CommandLineOptionBuilderInterface
     */
    protected $commandLineOptionBuilder;

    /**
     * @var string
     */
    protected $command;

    /**
     * @var array
     */
    protected $options = array();

    /**
     * @param \Stagehand\TestRunner\Core\Environment $environment
     * @param \Stagehand\TestRunner\Util\LegacyProxy $legacyProxy
     * @param \Stagehand\TestRunner\Util\OS $os
     * @param \Stagehand\TestRunner\Core\Plugin\PluginInterface $plugin
     * @param \Stagehand\TestRunner\Runner\Runner $runner
     * @param \Stagehand\TestRunner\CLI\Terminal $terminal
     * @param \Stagehand\TestRunner\Core\TestTargetRepository $testTargetRepository
     * @param \Stagehand\TestRunner\Process\ContinuousTesting\CommandLineOptionBuilderInterface $commandLineOptionBuilder
     */
    public function __construct(
        Environment $environment,
        LegacyProxy $legacyProxy,
        OS $os,
        PluginInterface $plugin,
        Runner $runner,
        Terminal $terminal,
        TestTargetRepository $testTargetRepository,
        CommandLineOptionBuilderInterface $commandLineOptionBuilder = null
        )
    {
        $this->environment = $environment;
        $this->legacyProxy = $legacyProxy;
        $this->os = $os;
        $this->plugin = $plugin;
        $this->runner = $runner;
        $this->terminal = $terminal;
        $this->testTargetRepository = $testTargetRepository;
        $this->commandLineOptionBuilder = $commandLineOptionBuilder;
    }

    /**
     * @return string
     */
    public function build()
    {
        $this->command = $this->buildCommand();
        $this->options = $this->buildOptions($this->command);

        return $this->command . ' ' . implode(' ', $this->options);
    }

    /**
     * @return string
     */
    protected function buildCommand()
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
     * @param string $command
     * @return array
     */
    protected function buildOptions($command)
    {
        $options = array();

        if (basename(trim($command, '\'"')) != 'testrunner') {
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

        $options[] = escapeshellarg(strtolower($this->plugin->getPluginID()));

        if (!is_null($this->environment->getPreloadScript())) {
            $options[] = '-p ' . escapeshellarg($this->environment->getPreloadScript());
        }

        $options[] = '-R';

        if ($this->runner->shouldNotify()) {
            $options[] = '-m';
        }

        if ($this->runner->shouldStopOnFailure()) {
            $options[] = '--stop-on-failure';
        }

        if (!$this->testTargetRepository->isDefaultFilePattern()) {
            $options[] = '--test-file-pattern=' . escapeshellarg($this->testTargetRepository->getFilePattern());
        }

        if ($this->runner->hasDetailedProgress()) {
            $options[] = '--detailed-progress';
        }

        if (!is_null($this->commandLineOptionBuilder)) {
            $options = $this->commandLineOptionBuilder->build($options);
        }

        $this->testTargetRepository->walkOnResources(function ($resource, $index, TestTargetRepository $testTargetRepository) use (&$options) {
            $options[] = escapeshellarg($resource);
        });

        return $options;
    }

    /**
     * @return string
     */
    protected function getPHPConfigDir()
    {
        return $this->legacyProxy->get_cfg_var('cfg_file_path');
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
