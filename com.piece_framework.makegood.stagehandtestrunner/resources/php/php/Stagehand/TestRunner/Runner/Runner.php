<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2008-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2008-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      File available since Release 2.3.0
 */

namespace Stagehand\TestRunner\Runner;

use Stagehand\TestRunner\CLI\Terminal;
use Stagehand\TestRunner\Core\TestTargetRepository;
use Stagehand\TestRunner\JUnitXMLWriter\DOMJUnitXMLWriter;
use Stagehand\TestRunner\JUnitXMLWriter\NullUTF8Converter;
use Stagehand\TestRunner\JUnitXMLWriter\StreamJUnitXMLWriter;
use Stagehand\TestRunner\JUnitXMLWriter\UTF8Converter;
use Stagehand\TestRunner\Util\FileStreamWriter;

/**
 * The base class for test runners.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2008-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      Class available since Release 2.3.0
 */
abstract class Runner
{
    /**
     * @var \Stagehand\TestRunner\Notification\Notification
     */
    protected $notification;

    /**
     * @var \Stagehand\TestRunner\CLI\Terminal
     * @since Property available since Release 3.0.0
     */
    protected $terminal;

    /**
     * @var string
     * @since Property available since Release 3.0.0
     */
    protected $junitXMLFile;

    /**
     * @var boolean
     * @since Property available since Release 3.3.0
     */
    protected $junitXMLRealtime;

    /**
     * @var boolean
     * @since Property available since Release 3.0.0
     */
    protected $stopOnFailure;

    /**
     * @var boolean
     * @since Property available since Release 3.0.0
     */
    protected $notify;

    /**
     * @var \Stagehand\TestRunner\Core\TestTargetRepository
     * @since Property available since Release 3.0.0
     */
    protected $testTargetRepository;

    /**
     * @var boolean
     * @since Property available since Release 3.0.0
     */
    protected $detailedProgress;

    /**
     * @param boolean $junitXMLRealtime
     * @since Method available since Release 3.3.0
     */
    public function setJUnitXMLRealtime($junitXMLRealtime)
    {
        $this->junitXMLRealtime = $junitXMLRealtime;
    }

    /**
     * Runs tests.
     *
     * @param mixed $suite
     */
    abstract public function run($suite);

    /**
     * Gets a notification object for Growl.
     *
     * @return \Stagehand\TestRunner\Notification\Notification
     */
    public function getNotification()
    {
        return $this->notification;
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
     * @param string $junitXMLFile
     * @since Method available since Release 3.0.0
     */
    public function setJUnitXMLFile($junitXMLFile)
    {
        $this->junitXMLFile = $junitXMLFile;
    }

    /**
     * @return boolean
     * @since Method available since Release 3.3.0
     */
    protected function hasJUnitXMLFile()
    {
        return !is_null($this->junitXMLFile);
    }

    /**
     * @param boolean $stopOnFailure
     * @since Method available since Release 3.0.0
     */
    public function setStopOnFailure($stopOnFailure)
    {
        $this->stopOnFailure = $stopOnFailure;
    }

    /**
     * @return boolean
     * @since Method available since Release 3.0.0
     */
    public function shouldStopOnFailure()
    {
        return $this->stopOnFailure;
    }

    /**
     * @param boolean $notify
     * @since Method available since Release 3.0.0
     */
    public function setNotify($notify)
    {
        $this->notify = $notify;
    }

    /**
     * @return boolean
     * @since Method available since Release 3.0.0
     */
    public function shouldNotify()
    {
        return $this->notify;
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
     * @param boolean $detailedProgress
     * @since Method available since Release 3.0.0
     */
    public function setDetailedProgress($detailedProgress)
    {
        $this->detailedProgress = $detailedProgress;
    }

    /**
     * @return boolean
     * @since Method available since Release 3.0.0
     */
    public function hasDetailedProgress()
    {
        return $this->detailedProgress;
    }

    /**
     * @param string $file
     * @return \Stagehand\TestRunner\Util\StreamWriterInterface
     * @since Method available since Release 3.0.0
     */
    protected function createStreamWriter($file)
    {
        return new FileStreamWriter($file);
    }

    /**
     * @return \Stagehand\TestRunner\JUnitXMLWriter\JUnitXMLWriter
     * @since Method available since Release 3.3.0
     */
    protected function createJUnitXMLWriter()
    {
        $streamWriter = $this->createStreamWriter($this->junitXMLFile);
        $utf8Converter = extension_loaded('mbstring') ? new UTF8Converter() : new NullUTF8Converter();
        return $this->junitXMLRealtime ? new StreamJUnitXMLWriter($streamWriter, $utf8Converter) : new DOMJUnitXMLWriter($streamWriter, $utf8Converter);
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
