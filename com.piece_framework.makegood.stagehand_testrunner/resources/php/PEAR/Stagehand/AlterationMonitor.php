<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2009 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @package    Stagehand_AlterationMonitor
 * @copyright  2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.1.0
 * @since      File available since Release 1.0.0
 */

// {{{ Stagehand_AlterationMonitor

/**
 * A file and directory alteration monitor.
 *
 * @package    Stagehand_AlterationMonitor
 * @copyright  2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.1.0
 * @since      Class available since Release 1.0.0
 */
class Stagehand_AlterationMonitor
{

    // {{{ constants

    const SCAN_INTERVAL_MIN = 5;
    const EVENT_CREATED = 1;
    const EVENT_CHANGED = 2;
    const EVENT_REMOVED = 4;

    // }}}
    // {{{ properties

    /**#@+
     * @access public
     */

    /**#@-*/

    /**#@+
     * @access protected
     */

    /**#@-*/

    /**#@+
     * @access private
     */

    protected $directories;
    protected $callback;
    protected $invokesCallbackForEachFile;
    protected $scanInterval = self::SCAN_INTERVAL_MIN;
    protected $directoryScanner;
    protected $isFirstTime = true;
    protected $currentElements = array();
    protected $previousElements = array();
    protected $eventQueue = array();

    /**#@-*/

    /**#@+
     * @access public
     */

    // }}}
    // {{{ __construct()

    /**
     * Sets one or more target directories and a callback to the properties.
     *
     * @param array    $directories
     * @param callback $callback
     * @param boolean  $invokesCallbackForEachFile
     */
    public function __construct($directories,
                                $callback,
                                $invokesCallbackForEachFile = false
                                )
    {
        $this->directories = $directories;
        $this->callback = $callback;
        $this->invokesCallbackForEachFile = $invokesCallbackForEachFile;
        $this->directoryScanner =
            new Stagehand_DirectoryScanner(array($this, 'detectChanges'));
    }

    // }}}
    // {{{ monitor()

    /**
     * Watches for changes in the target directories and invokes the callback when
     * changes are detected.
     */
    public function monitor()
    {
        while (true) {
            $this->waitForChanges();
            $this->invokeCallback();
        }
    }

    // }}}
    // {{{ detectChanges()

    /**
     * Detects any changes of a file or directory immediately.
     *
     * @param string $file
     * @throws Stagehand_AlterationMonitor_Exception
     */
    public function detectChanges($file)
    {
        $perms = fileperms($file);
        if ($perms === false) {
            throw new Stagehand_AlterationMonitor_Exception();
        }

        $this->currentElements[$file] = array('perms' => $perms,
                                              'mtime' => null,
                                              'isDirectory' => is_dir($file)
                                              );

        if (!$this->currentElements[$file]['isDirectory']) {
            $mtime = filemtime($file);
            if ($mtime === false) {
                throw new Stagehand_AlterationMonitor_Exception();
            }

            $this->currentElements[$file]['mtime'] = $mtime;
        }

        if ($this->isFirstTime) {
            return;
        }

        $perms = fileperms($file);
        if ($perms === false) {
            throw new Stagehand_AlterationMonitor_Exception();
        }

        if (!array_key_exists($file, $this->previousElements)) {
            $this->addEvent(self::EVENT_CREATED, $file);
            return;
        }

        $isDirectory = is_dir($file);
        if ($this->currentElements[$file]['isDirectory'] != $isDirectory) {
            $this->addEvent(self::EVENT_CHANGED, $file);
            return;
        }

        if ($this->previousElements[$file]['perms'] != $perms) {
            $this->addEvent(self::EVENT_CHANGED, $file);
            return;
        }

        if ($isDirectory) {
            return;
        }

        $mtime = filemtime($file);
        if ($mtime === false) {
            throw new Stagehand_AlterationMonitor_Exception();
        }

        if ($this->previousElements[$file]['mtime'] != $mtime) {
            $this->addEvent(self::EVENT_CHANGED, $file);
            return;
        }
    }

    /**#@-*/

    /**#@+
     * @access protected
     */

    // }}}
    // {{{ waitForChanges()

    /**
     * Watches for changes in the target directories and returns immediately when
     * changes are detected.
     */
    protected function waitForChanges()
    {
        try {
            while (true) {
                sleep($this->scanInterval);
                clearstatcache();

                $startTime = time();
                foreach ($this->directories as $directory) {
                    $this->directoryScanner->scan($directory);
                }
                $endTime = time();
                $elapsedTime = $endTime - $startTime;
                if ($elapsedTime > self::SCAN_INTERVAL_MIN) {
                    $this->scanInterval = $elapsedTime;
                }

                if (!$this->isFirstTime) {
                    reset($this->previousElements);
                    while (list($file, $stat) = each($this->previousElements)) {
                        if (!array_key_exists($file, $this->currentElements)) {
                            $this->addEvent(self::EVENT_REMOVED, $file);
                        }
                    }
                }

                $this->previousElements = $this->currentElements;
                $this->currentElements = array();
                $this->isFirstTime = false;

                if (count($this->eventQueue)) {
                    throw new Stagehand_AlterationMonitor_AlterationException();
                }
            }
        } catch (Stagehand_AlterationMonitor_AlterationException $e) {
        }
    }

    // }}}
    // {{{ addEvent()

    /**
     * @param integer $event
     * @param string  $file
     */
    protected function addEvent($event, $file)
    {
        $this->eventQueue[] = new Stagehand_AlterationMonitor_Event($event, $file);
    }

    // }}}
    // {{{ invokeCallback()

    /**
     */
    protected function invokeCallback()
    {
        if (!$this->invokesCallbackForEachFile) {
            call_user_func($this->callback, $this->eventQueue);
        } else {
            foreach ($this->eventQueue as $event) {
                call_user_func($this->callback, $event);
            }
        }

        $this->eventQueue = array();
    }

    /**#@-*/

    /**#@+
     * @access private
     */

    /**#@-*/

    // }}}
}

// }}}

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
