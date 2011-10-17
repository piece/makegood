<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
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
 * @version    Release: 2.20.0
 * @since      File available since Release 2.16.0
 */

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.20.0
 * @since      Class available since Release 2.16.0
 */
class Stagehand_TestRunner_Preparer_CIUnitPreparer extends Stagehand_TestRunner_Preparer_PHPUnitPreparer
{
    /**
     * @var array
     */
    protected $superglobals = array(
        '_GET' => null,
        'PATH_INFO' => null,
        'QUERY_STRING' => null,
    );

    /**
     * @var array
     */
    protected $environmentVariables = array(
        'PATH_INFO' => null,
        'QUERY_STRING' => null,
    );

    public function prepare()
    {
        parent::prepare();

        if (is_null($this->config->ciunitPath)) {
            $ciunitPath = $this->config->getWorkingDirectoryAtStartup();
        } else {
            $ciunitPath = $this->config->ciunitPath;
        }

        /* Removes some superglobals and environment variables to avoid getting invalid
         * URI string by the CIUnit URI object since some cases PDT sets some
         * environment variables for debugging.
         */
        $this->backupVariables();
        require_once $ciunitPath . '/CIUnit.php';
        $this->restoreVariables();
    }

    protected function backupVariables()
    {
        if (isset($_GET)) {
            $this->superglobals['_GET'] = $_GET;
            foreach (array_keys($_GET) as $key) {
                unset($_GET[$key]);
            }
        }

        if (array_key_exists('PATH_INFO', $_SERVER)) {
            $this->superglobals['PATH_INFO'] = $_SERVER['PATH_INFO'];
            unset($_SERVER['PATH_INFO']);
        }
        if (getenv('PATH_INFO') !== false) {
            $this->environmentVariables['PATH_INFO'] = getenv('PATH_INFO');
            putenv('PATH_INFO');
        }

        if (array_key_exists('QUERY_STRING', $_SERVER)) {
            $this->superglobals['QUERY_STRING'] = $_SERVER['QUERY_STRING'];
            unset($_SERVER['QUERY_STRING']);
        }
        if (getenv('QUERY_STRING') !== false) {
            $this->environmentVariables['QUERY_STRING'] = getenv('QUERY_STRING');
            putenv('QUERY_STRING');
        }
    }

    protected function restoreVariables()
    {
        if (!is_null($this->superglobals['_GET'])) {
            foreach (array_keys($this->superglobals['_GET']) as $key) {
                $_GET[$key] = $this->superglobals['_GET'][$key];
            }
        }

        if (!is_null($this->superglobals['PATH_INFO'])) {
            $_SERVER['PATH_INFO'] = $this->superglobals['PATH_INFO'];
        }
        if (!is_null($this->environmentVariables['PATH_INFO'])) {
            putenv('PATH_INFO=' . $this->environmentVariables['PATH_INFO']);
        }

        if (!is_null($this->superglobals['QUERY_STRING'])) {
            $_SERVER['QUERY_STRING'] = $this->superglobals['QUERY_STRING'];
        }
        if (!is_null($this->environmentVariables['QUERY_STRING'])) {
            putenv('QUERY_STRING=' . $this->environmentVariables['QUERY_STRING']);
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
