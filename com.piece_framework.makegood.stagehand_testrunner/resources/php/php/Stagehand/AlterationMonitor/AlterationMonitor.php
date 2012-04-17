<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2009, 2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2009, 2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.0.1
 * @since      File available since Release 1.0.0
 */

namespace Stagehand\AlterationMonitor;

use Symfony\Component\Finder\Finder;

/**
 * A file and directory alteration monitor.
 *
 * @package    Stagehand_AlterationMonitor
 * @copyright  2009, 2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.0.1
 * @since      Class available since Release 1.0.0
 */
class AlterationMonitor
{
    protected $directories;
    protected $callback;
    protected $scanInterval;
    protected $firstTime = true;
    protected $currentResources = array();
    protected $previousResources = array();
    protected $resourceChangeEvents = array();

    /**
     * Sets one or more target directories and a callback to the properties.
     *
     * @param array    $directories
     * @param integer $scanInterval
     * @param callback $callback
     */
    public function __construct($directories, $scanInterval, $callback)
    {
        $this->directories = $directories;
        $this->callback = $callback;
        $this->scanInterval = $scanInterval;
    }

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

    /**
     * Detects any changes of a file or directory immediately.
     *
     * @param string $resource
     */
    public function detectChanges($resource)
    {
        try {
            $perms = fileperms($resource);
            $isDirectory = is_dir($resource);
            if (!$isDirectory) {
                $mtime = filemtime($resource);
            } else {
                $mtime = null;
            }
        } catch (\ErrorException $e) {
            return;
        }

        $this->currentResources[$resource] = array(
            'perms' => $perms,
            'mtime' => $mtime,
            'isDirectory' => $isDirectory
        );

        if ($this->firstTime) {
            return;
        }

        if (!array_key_exists($resource, $this->previousResources)) {
            $this->addEvent(ResourceChangeEvent::EVENT_CREATED, $resource);
            return;
        }

        if ($this->currentResources[$resource]['isDirectory'] != $this->previousResources[$resource]['isDirectory']) {
            $this->addEvent(ResourceChangeEvent::EVENT_CHANGED, $resource);
            return;
        }

        if ($this->currentResources[$resource]['perms'] != $this->previousResources[$resource]['perms']) {
            $this->addEvent(ResourceChangeEvent::EVENT_CHANGED, $resource);
            return;
        }

        if ($this->currentResources[$resource]['isDirectory']) {
            return;
        }

        if ($this->currentResources[$resource]['mtime'] > $this->previousResources[$resource]['mtime']) {
            $this->addEvent(ResourceChangeEvent::EVENT_CHANGED, $resource);
            return;
        }
    }

    /**
     * Watches for changes in the target directories and returns immediately when
     * changes are detected.
     *
     * @throws \Exception
     */
    protected function waitForChanges()
    {
        set_error_handler(function ($code, $message, $file, $line) {
            if (error_reporting() & $code) {
                throw new \ErrorException($message, 0, $code, $file, $line);
            }
        });

        try {
            while (true) {
                clearstatcache();

                foreach (Finder::create()->in($this->directories) as $resource) {
                    $this->detectChanges($resource->getPathname());
                }

                if (!$this->firstTime) {
                    reset($this->previousResources);
                    while (list($resource, $stat) = each($this->previousResources)) {
                        if (!array_key_exists($resource, $this->currentResources)) {
                            $this->addEvent(ResourceChangeEvent::EVENT_REMOVED, $resource);
                        }
                    }
                }

                $this->previousResources = $this->currentResources;
                $this->currentResources = array();
                $this->firstTime = false;

                if (count($this->resourceChangeEvents)) {
                    break;
                }

                usleep($this->scanInterval);
            }
        } catch (\Exception $e) {
            restore_error_handler();
            throw $e;
        }

        restore_error_handler();
    }

    /**
     * @param integer $event
     * @param string  $resource
     */
    protected function addEvent($event, $resource)
    {
        $this->resourceChangeEvents[] = new ResourceChangeEvent($event, new \SplFileInfo($resource));
    }

    /**
     */
    protected function invokeCallback()
    {
        call_user_func($this->callback, $this->resourceChangeEvents);
        $this->resourceChangeEvents = array();
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
