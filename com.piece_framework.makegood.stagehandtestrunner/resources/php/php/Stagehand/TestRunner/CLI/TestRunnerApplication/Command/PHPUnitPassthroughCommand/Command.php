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

namespace Stagehand\TestRunner\CLI\TestRunnerApplication\Command\PHPUnitPassthroughCommand;

use Stagehand\TestRunner\Collector\Collector;
use Stagehand\TestRunner\Core\TestTargetRepository;
use Stagehand\TestRunner\Util\FileSystem;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.0
 * @since      Class available since Release 3.6.0
 */
class Command extends \PHPUnit_TextUI_Command
{
    /**
     * @var \Stagehand\TestRunner\Collector\Collector
     */
    protected $collector;

    /**
     * @var \Stagehand\TestRunner\Core\TestTargetRepository
     */
    protected $testTargetRepository;

    /**
     * @param \Stagehand\TestRunner\Collector\Collector $collector
     * @param \Stagehand\TestRunner\Core\TestTargetRepository $testTargetRepository
     */
    public function __construct(Collector $collector, TestTargetRepository $testTargetRepository)
    {
        $this->collector = $collector;
        $this->testTargetRepository = $testTargetRepository;
    }

    /**
     * {@inheritDoc}
     */
    protected function createRunner()
    {
        $runner = new TestRunner($this->arguments['loader']);
        $runner->setCollector($this->collector);
        $runner->setTestTargetRepository($this->testTargetRepository);

        return $runner;
    }

    /**
     * {@inheritDoc}
     */
    protected function handleArguments(array $argv)
    {
        parent::handleArguments($argv);

        if (array_key_exists('testSuffixes', $this->arguments)) {
            $fileSystem = new FileSystem();
            $filePatterns = array();

            foreach ($this->arguments['testSuffixes'] as $testSuffix) {
                $filePatterns[] = preg_quote($testSuffix) . '$';
            }

            $this->testTargetRepository->setResources(array(empty($this->arguments['testFile']) ? $this->arguments['test'] : $this->arguments['testFile']));
            $this->testTargetRepository->setFilePattern(implode('|', $filePatterns));
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
