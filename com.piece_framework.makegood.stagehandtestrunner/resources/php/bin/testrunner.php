#!/usr/bin/env php
<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2011-2012, 2014 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2011-2012, 2014 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: @package_version@
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\Core;

try {
    loadAutoloader();
    initializeEnvironment();
    workingDirectoryAtStartup();
    preloadScript();
} catch (\Exception $e) {
    echo $e->getMessage() . PHP_EOL;
    exit(1);
}

$bootstrap = new \Stagehand\TestRunner\Core\Bootstrap();
$bootstrap->boot();

$application = new \Stagehand\TestRunner\CLI\TestRunnerApplication\Application();
$result = $application->run();

exit($result);

/**
 * @since Method available since Release 4.0.0
 * @throws \RuntimeException
 */
function loadAutoloader()
{
    $autoloadFileForProject = dirname(dirname(dirname(__DIR__))) . '/autoload.php';
    $autoloadFileForStagehandTestRunner = dirname(__DIR__) . '/vendor/autoload.php';

    foreach (array($autoloadFileForProject, $autoloadFileForStagehandTestRunner) as $autoloadFile) {
        if (file_exists($autoloadFile)) {
            require $autoloadFile;

            return;
        }
    }

    throw new \RuntimeException(sprintf('The autoload file "%s" is not found. Check your Composer environment.', $autoloadFileForProject));
}

/**
 * @since Method available since Release 3.4.0
 */
function initializeEnvironment()
{
    ini_set('display_errors', true);
    ini_set('log_errors', false);
    ini_set('html_errors', false);
    ini_set('implicit_flush', true);
    ini_set('max_execution_time', 0);
    ini_set('memory_limit', -1);
}

/**
 * Gets the current working directory at startup.
 *
 * @throws \UnexpectedValueException
 * @since Method available since Release 3.4.0
 */
function workingDirectoryAtStartup()
{
    static $workingDirectoryAtStartup;

    if (is_null($workingDirectoryAtStartup)) {
        $workingDirectoryAtStartup = getcwd();
        if ($workingDirectoryAtStartup === false) {
            throw new \UnexpectedValueException('Cannot get the current working directory. Make sure the current working directory path and permission are correct.');
        }
    }

    return $workingDirectoryAtStartup;
}

/**
 * Finds the preload option and preloads a file as a PHP script if it is specified.
 *
 * @throws \UnexpectedValueException
 * @since Method available since Release 3.4.0
 */
function preloadScript()
{
    static $initialized = false;
    static $preloadScript;

    if (!$initialized) {
        $initialized = true;

        for ($i = 1, $count = count($_SERVER['argv']); $i < $count; ++$i) {
            $arg = $_SERVER['argv'][$i];
            if (strlen($arg) <= 1) continue;

            if (preg_match('/^--preload-script(?:=(.*))?$/', $arg, $matches)) {
                if (count($matches) == 1) {
                    if (array_key_exists($i + 1, $_SERVER['argv'])) {
                        $preloadScript = $_SERVER['argv'][ $i + 1 ];
                        break;
                    }
                } elseif (count($matches) == 2) {
                    $preloadScript = $matches[1];
                    break;
                }
            } elseif (preg_match('/^-[a-oq-zA-OQ-Z]*p(.*)$/', $arg, $matches)) {
                if (strlen($matches[1]) == 0) {
                    if (array_key_exists($i + 1, $_SERVER['argv'])) {
                        $preloadScript = $_SERVER['argv'][ $i + 1 ];
                        break;
                    }
                } else {
                    $preloadScript = $matches[1];
                    break;
                }
            }
        }

        if (!is_null($preloadScript)) {
            $result = include_once $preloadScript;
            if (!$result) {
                throw new \UnexpectedValueException(sprintf(
                    'Cannot load [ %s ]. Make sure the file path and permission are correct.',
                    $preloadScript
                ));
            }
        }
    }

    return $preloadScript;
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
