#!/usr/bin/env php
<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2007-2011 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2007-2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.20.0
 * @since      File available since Release 1.0.0
 */

// Gets the current directory at startup.
$GLOBALS['STAGEHAND_TESTRUNNER_CONFIG_workingDirectoryAtStartup'] = getcwd();

// Finds the preload option and preloads a file as a PHP script if it is specified.
$preload = create_function('', '
$preloadFile = null;
do {
    for ($i = 1, $count = count($_SERVER[\'argv\']); $i < $count; ++$i) {
        $arg = $_SERVER[\'argv\'][$i];
        if (strlen($arg) <= 1) continue;
        if (substr($arg, 0, 1) != \'-\') continue;
        if (substr($arg, 1, 1) == \'-\') continue;
        if (!preg_match(\'/^-[^p]*?p(.*)$/\', $arg, $matches)) continue;
        if (strlen($matches[1]) == 0) {
            if (array_key_exists($i + 1, $_SERVER[\'argv\'])) {
                $preloadFile = $_SERVER[\'argv\'][ $i + 1 ];
                break 2;
            }
        } else {
            $preloadFile = $matches[1];
            break 2;
        }
    }
    return;
} while (false);
if (is_null($preloadFile)) return;
$result = include_once $preloadFile;
if (!$result) {
    echo "ERROR: Cannot load [ $preloadFile ]. Make sure the file path and permission are correct.\n";
    exit(1);
}
');
$preload();

require_once 'Stagehand/Autoload.php';

$loader = Stagehand_Autoload::legacyLoader();
$loader->addNamespace('Stagehand');
Stagehand_Autoload::register($loader);

$controller = new Stagehand_TestRunner_TestRunnerCLIController(Stagehand_TestRunner_Framework::PHPUNIT);
$result = $controller->run();

exit($result);

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
