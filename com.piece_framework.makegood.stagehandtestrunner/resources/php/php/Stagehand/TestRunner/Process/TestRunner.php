<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2010-2013 KUBO Atsuhiro <kubo@iteman.jp>,
 *               2011 Shigenobu Nishikawa <shishi.s.n@gmail.com>,
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
 * @copyright  2010-2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @copyright  2011 Shigenobu Nishikawa <shishi.s.n@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.0
 * @since      File available since Release 2.14.0
 */

namespace Stagehand\TestRunner\Process;

use Stagehand\TestRunner\Collector\Collector;
use Stagehand\TestRunner\Notification\Notifier;
use Stagehand\TestRunner\Preparer\Preparer;
use Stagehand\TestRunner\Runner\Runner;
use Stagehand\TestRunner\Util\OutputBuffering;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2010-2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @copyright  2011 Shigenobu Nishikawa <shishi.s.n@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.0
 * @since      Class available since Release 2.14.0
 */
class TestRunner implements TestRunnerInterface
{
    /**
     * @var boolean $result
     * @since Property available since Release 2.18.0
     */
    protected $result;

    /**
     * @var \Stagehand\TestRunner\Util\OutputBuffering
     * @since Property available since Release 3.0.0
     */
    protected $outputBuffering;

    /**
     * @var \Stagehand\TestRunner\Collector\Collector
     * @since Property available since Release 3.6.0
     */
    protected $collector;

    /**
     * @var \Stagehand\TestRunner\Runner\Runner
     * @since Property available since Release 3.6.0
     */
    protected $runner;

    /**
     * @var \Stagehand\TestRunner\Notification\Notifier
     * @since Property available since Release 3.6.0
     */
    protected $notifier;

    /**
     * @param \Stagehand\TestRunner\Preparer\Preparer $preparer
     * @since Method available since Release 3.6.0
     */
    public function __construct(Preparer $preparer)
    {
        $preparer->prepare();
    }

    /**
     * Runs tests.
     *
     * @since Method available since Release 2.1.0
     */
    public function run()
    {
        $this->outputBuffering->clearOutputHandlers();

        $this->result = $this->runner->run($this->collector->collect());

        if ($this->runner->shouldNotify()) {
            $this->notifier->notifyResult($this->runner->getNotification());
        }
    }

    /**
     * @param \Stagehand\TestRunner\Util\OutputBuffering $outputBuffering
     * @since Method available since Release 3.0.0
     */
    public function setOutputBuffering(OutputBuffering $outputBuffering)
    {
        $this->outputBuffering = $outputBuffering;
    }

    /**
     * @param \Stagehand\TestRunner\Collector\Collector $collector
     * @since Method available since Release 3.6.0
     */
    public function setCollector(Collector $collector)
    {
        $this->collector = $collector;
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
