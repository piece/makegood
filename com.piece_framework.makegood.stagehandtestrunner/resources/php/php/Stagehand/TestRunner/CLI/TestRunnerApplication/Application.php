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
 * @version    Release: 3.6.0
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\CLI\TestRunnerApplication;

use Symfony\Component\Console\Input\InputArgument;
use Symfony\Component\Console\Input\InputDefinition;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Input\InputOption;

use Stagehand\TestRunner\CLI\TestRunnerApplication\Command\CommandRepository;
use Stagehand\TestRunner\Core\ApplicationContext;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.0
 * @since      Class available since Release 3.0.0
 */
class Application extends \Symfony\Component\Console\Application
{
    /**
     * @var \Stagehand\TestRunner\CLI\TestRunnerApplication\Command\CommandRepository
     */
    protected $commandRepository;

    public function __construct()
    {
        $this->commandRepository = new CommandRepository();
        parent::__construct('Stagehand_TestRunner', '3.6.0');
        $this->setAutoExit(false);

        // For compatibility with Symfony 2.0
        if (!method_exists('Symfony\Component\Console\Application', 'getDefaultCommands')) {
            foreach ($this->getDefaultCommands() as $command) {
                $this->add($command);
            }
        }
    }

    public function getDefinition()
    {
        if (method_exists('Symfony\Component\Console\Application', 'getDefaultCommands')) {
            return parent::getDefinition();
        } else {
            // For compatibility with Symfony 2.0
            static $definition;

            if (is_null($definition)) {
                $definition = $this->getDefaultInputDefinition();
            }
            return $definition;
        }
    }

    public function getLongVersion()
    {
        return
            parent::getLongVersion() . PHP_EOL .
            PHP_EOL .
            $this->getCopyright();
    }

    protected function getCommandName(InputInterface $input)
    {
        $commandName = parent::getCommandName($input);
        if (is_null($commandName)) {
            return null;
        } else {
            if ($commandName == ApplicationContext::getInstance()->getEnvironment()->getPreloadScript()) {
                return null;
            } else {
                return $commandName;
            }
        }
    }

    protected function getDefaultInputDefinition()
    {
        return new InputDefinition(array(
            new InputArgument('command', InputArgument::REQUIRED, 'The command to execute'),
            new InputOption('help', 'h', InputOption::VALUE_NONE, 'Prints help and exit.'),
            new InputOption('version', 'V', InputOption::VALUE_NONE, 'Prints version information and exit.'),
            new InputOption('ansi', null, InputOption::VALUE_NONE, 'Enables ANSI output.'),
            new InputOption('no-ansi', null, InputOption::VALUE_NONE, 'Disables ANSI output.'),
        ));
    }

    protected function getDefaultCommands()
    {
        return $this->commandRepository->findAll();
    }

    /**
     * @return string
     */
    protected function getCopyright()
    {
        return
'Copyright (c) 2005-2013 KUBO Atsuhiro and contributors,' . PHP_EOL .
'All rights reserved.';
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
