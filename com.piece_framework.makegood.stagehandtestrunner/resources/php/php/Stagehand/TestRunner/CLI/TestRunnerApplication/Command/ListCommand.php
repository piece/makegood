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
 * @version    Release: 3.5.0
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\CLI\TestRunnerApplication\Command;

use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.5.0
 * @since      Class available since Release 3.0.0
 */
class ListCommand extends Command
{
    protected function configure()
    {
        parent::configure();

        $this->setName('list');
        $this->setDescription('Lists commands.');
        $this->setHelp(
'The <info>list</info> command lists all commands:' . PHP_EOL .
PHP_EOL .
'  <info>testrunner list</info>'
        );
    }

    protected function execute(InputInterface $input, OutputInterface $output)
    {
        $output->writeln($this->buildMessage());
    }

    protected function buildMessage()
    {
        $commands = $this->getApplication()->all();

        $width = 0;
        foreach ($commands as $command) {
            $width = strlen($command->getName()) > $width ? strlen($command->getName()) : $width;
        }
        $width += 2;

        $generalCommands = array();
        $pluginCommands = array();
        foreach ($commands as $name => $command) {
            if ($command instanceof PluginCommand) {
                $pluginCommands[$name] = $command;
            } else {
                $generalCommands[$name] = $command;
            }
        }

        $messages = array($this->getApplication()->getHelp(), '');
        $buildCommandMessage = function (\Symfony\Component\Console\Command\Command $command) use (&$messages, $width) {
            $messages[] = sprintf(
                '  <info>%-' . $width . 's</info> %s',
                $command->getName(),
                $command->getDescription()
            );
        };

        $messages[] = '<comment>Testing Framework Commands:</comment>';
        ksort($pluginCommands);
        array_map($buildCommandMessage, array_values($pluginCommands));

        $messages[] = '<comment>Other Commands:</comment>';
        ksort($generalCommands);
        array_map($buildCommandMessage, array_values($generalCommands));

        return implode(PHP_EOL, $messages);
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
