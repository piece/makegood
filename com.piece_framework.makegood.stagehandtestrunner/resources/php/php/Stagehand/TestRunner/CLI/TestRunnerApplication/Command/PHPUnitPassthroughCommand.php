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

namespace Stagehand\TestRunner\CLI\TestRunnerApplication\Command;

use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Input\InputOption;
use Symfony\Component\Console\Output\OutputInterface;

use Stagehand\TestRunner\Core\ApplicationContext;
use Stagehand\TestRunner\Core\Plugin\PHPUnitPlugin;
use Stagehand\TestRunner\Core\Plugin\PluginRepository;
use Stagehand\TestRunner\DependencyInjection\Compiler\Compiler;
use Stagehand\TestRunner\DependencyInjection\PHPUnitContainer;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.0
 * @since      Class available since Release 3.6.0
 */
class PHPUnitPassthroughCommand extends Command
{
    /**
     * {@inheritDoc}
     */
    protected function configure()
    {
        parent::configure();

        $this->ignoreValidationErrors();
        $this->setName('phpunit:passthrough');
        $this->setDescription('Runs the phpunit command via the testrunner command.');
        $this->setHelp(
'The <info>' . $this->getName() . '</info> command runs the command which is equivalent to the phpunit command except it allows you to declare multiple test classes in a file:' . PHP_EOL .
PHP_EOL .
'  <info>testrunner ' . $this->getName() . ' ...</info>'
        );
        $this->addOption('phpunit-help', null, InputOption::VALUE_NONE, 'Prints the usage information for the phpunit command.');
    }

    /**
     * {@inheritDoc}
     */
    protected function execute(InputInterface $input, OutputInterface $output)
    {
        if (!class_exists(Compiler::COMPILED_CONTAINER_NAMESPACE . '\\PHPUnitContainer')) {
            $output->writeln(
'<error>Please run the following command before running the ' . $this->getName() . ' command:</error>' . PHP_EOL .
PHP_EOL .
'  <info>testrunner compile</info>'
            );

            return 1;
        }

        $container = $this->createContainer();
        ApplicationContext::getInstance()->getComponentFactory()->setContainer($container);
        ApplicationContext::getInstance()->setComponent('environment', ApplicationContext::getInstance()->getEnvironment());
        ApplicationContext::getInstance()->setComponent('input', $input);
        ApplicationContext::getInstance()->setComponent('output', $output);
        ApplicationContext::getInstance()->setComponent('plugin', PluginRepository::findByPluginID(PHPUnitPlugin::getPluginID()));

        ApplicationContext::getInstance()->createComponent('preparer')->prepare();
        $collector = ApplicationContext::getInstance()->createComponent('collector');
        $collector->setRecursive(true);

        define('PHPUnit_MAIN_METHOD', __METHOD__);
        $command = new \Stagehand\TestRunner\CLI\TestRunnerApplication\Command\PHPUnitPassthroughCommand\Command($collector, ApplicationContext::getInstance()->createComponent('test_target_repository'));

        if ($input->getOption('phpunit-help')) {
            return $command->run(array_merge($this->removeTestRunnerArguments($_SERVER['argv'], array('--phpunit-help')), array('--help')), false);
        }

        return $command->run($this->removeTestRunnerArguments($_SERVER['argv']), false);
    }

    /**
     * @return \Symfony\Component\DependencyInjection\ContainerInterface
     */
    protected function createContainer()
    {
        return new PHPUnitContainer();
    }

    /**
     * @param array $argv
     * @param array $removingArgs
     * @return array
     */
    protected function removeTestRunnerArguments(array $argv, array $removingArgs = array())
    {
        $newArgv = array();

        for ($i = 0, $count = count($argv); $i < $count; ++$i) {
            $arg = $argv[$i];
            if (preg_match('/^--preload-script(?:=(.*))?$/', $arg, $matches)) {
                if (count($matches) == 1) {
                    if (array_key_exists($i + 1, $argv)) {
                        ++$i;
                        continue;
                    }
                } elseif (count($matches) == 2) {
                    continue;
                }
            } elseif (preg_match('/^-[a-oq-zA-OQ-Z]*p(.*)$/', $arg, $matches)) {
                if (strlen($matches[1]) == 0) {
                    if (array_key_exists($i + 1, $argv)) {
                        ++$i;
                        continue;
                    }
                } else {
                    continue;
                }
            } elseif ($arg == $this->getName()) {
                continue;
            } else {
                if (in_array($arg, $removingArgs)) {
                    continue;
                }
            }

            $newArgv[] = $arg;
        }

        return $newArgv;
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
