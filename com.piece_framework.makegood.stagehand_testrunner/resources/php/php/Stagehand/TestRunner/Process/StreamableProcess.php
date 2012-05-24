<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
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
 * @version    Release: 3.0.3
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\Process;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.0.3
 * @since      Class available since Release 3.0.0
 */
class StreamableProcess
{
    /**
     * @var string
     */
    protected $command;

    /**
     * @var array
     */
    protected $outputStreamListeners = array();

    /**
     * @var array
     */
    protected $errorStreamListeners = array();

    /**
     * @param string $command
     */
    public function __construct($command)
    {
        $this->command = $command;
    }

    /**
     * @return integer
     * @throws \Stagehand\TestRunner\Process\StreamException
     */
    public function run()
    {
        $process = proc_open(
            $this->command,
            array(1 => array('pipe', 'w'), 2 => array('pipe', 'w')),
            $pipes
        );
        stream_set_blocking($pipes[1], 0);
        stream_set_blocking($pipes[2], 0);

        while (!feof($pipes[1]) || !feof($pipes[2])) {
            $readingStreams = array($pipes[1], $pipes[2]);
            $writingStreams = null;
            $exceptStreams = null;
            $changedStreamCount = stream_select($readingStreams, $writingStreams, $exceptStreams, 1);
            if ($changedStreamCount === false) {
                throw new StreamException('An error is raised during waiting for any data to be written to the standard output or standard error.');
            }

            if ($changedStreamCount > 0) {
                foreach ($readingStreams as $readingStream) {
                    $output = fread($readingStream, 8192);
                    if ($readingStream === $pipes[1]) {
                        foreach ($this->outputStreamListeners as $outputStreamListener) {
                            call_user_func($outputStreamListener, $output);
                        }
                    } elseif ($readingStream === $pipes[2]) {
                        foreach ($this->errorStreamListeners as $errorStreamListener) {
                            call_user_func($errorStreamListener, $output);
                        }
                    }
                }
            }
        }

        fclose($pipes[1]);
        fclose($pipes[2]);
        return proc_close($process);
    }

    /**
     * @param \Closure $outputStreamListener
     */
    public function addOutputStreamListener(\Closure $outputStreamListener)
    {
        $this->outputStreamListeners[] = $outputStreamListener;
    }

    /**
     * @param \Closure $errorStreamListener
     */
    public function addErrorStreamListener(\Closure $errorStreamListener)
    {
        $this->errorStreamListeners[] = $errorStreamListener;
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
