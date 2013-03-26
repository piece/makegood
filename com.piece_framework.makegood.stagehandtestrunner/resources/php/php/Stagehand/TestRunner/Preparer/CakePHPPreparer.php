<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2010-2013 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      File available since Release 2.14.0
 */

namespace Stagehand\TestRunner\Preparer;

use Stagehand\TestRunner\Core\Environment;
use Stagehand\TestRunner\Preparer\CakePHPPreparer\TestRunnerShellDispatcher;
use Stagehand\TestRunner\Util\ErrorReporting;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2010-2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      Class available since Release 2.14.0
 */
class CakePHPPreparer extends Preparer
{
    /**
     * @var string
     * @since Property available since Release 3.0.0
     */
    protected $cakephpAppPath;

    /**
     * @var string
     * @since Property available since Release 3.0.0
     */
    protected $cakephpCorePath;

    /**
     * @var \Stagehand\TestRunner\Core\Environment
     * @since Property available since Release 3.6.0
     */
    protected $environment;

    /**
     * @param \Stagehand\TestRunner\Core\Environment $environment
     * @since Method available since Release 3.6.0
     */
    public function setEnvironment(Environment $environment)
    {
        $this->environment = $environment;
    }

    public function prepare()
    {
        if (defined('STAGEHAND_TESTRUNNER_PREPARER_CAKEPREPARER_PREPARECALLEDMARKER')) {
            return;
        }

        define('STAGEHAND_TESTRUNNER_PREPARER_CAKEPREPARER_PREPARECALLEDMARKER', true);

        if (!defined('DISABLE_AUTO_DISPATCH')) {
            define('DISABLE_AUTO_DISPATCH', true);
        }

        if (is_null($this->getCakePHPAppPath())) {
            $cakephpAppPath = $this->environment->getWorkingDirectoryAtStartup();
        } else {
            $cakephpAppPath = $this->getCakePHPAppPath();
        }

        $rootPath = realpath($cakephpAppPath . '/..');
        $appPath = basename(realpath($cakephpAppPath));
        if (is_null($this->getCakePHPCorePath())) {
            $corePath = $rootPath . DIRECTORY_SEPARATOR . 'cake';
        } else {
            $corePath = realpath($this->getCakePHPCorePath());
        }

        if (!defined('TEST_CAKE_CORE_INCLUDE_PATH')) {
            define('TEST_CAKE_CORE_INCLUDE_PATH', rtrim($corePath, DIRECTORY_SEPARATOR) . DIRECTORY_SEPARATOR);
        }

        ob_start();
        require_once $corePath . '/console/cake.php';
        ErrorReporting::invokeWith(error_reporting(), function () use ($rootPath, $appPath) {
            new TestRunnerShellDispatcher(array('-root', $rootPath, '-app', $appPath));
        });
        ob_end_clean();
        require_once $corePath . '/tests/lib/test_manager.php';
        new \TestManager();
    }

    /**
     * @param string $cakephpAppPath
     * @since Method available since Release 3.0.0
     */
    public function setCakePHPAppPath($cakephpAppPath)
    {
        $this->cakephpAppPath = $cakephpAppPath;
    }

    /**
     * @return string
     * @since Method available since Release 3.0.0
     */
    public function getCakePHPAppPath()
    {
        return $this->cakephpAppPath;
    }

    /**
     * @param string $cakephpCorePath
     * @since Method available since Release 3.0.0
     */
    public function setCakePHPCorePath($cakephpCorePath)
    {
        $this->cakephpCorePath = $cakephpCorePath;
    }

    /**
     * @return string
     * @since Method available since Release 3.0.0
     */
    public function getCakePHPCorePath()
    {
        return $this->cakephpCorePath;
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
