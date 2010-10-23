<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2010-2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.17.0
 * @since      File available since Release 2.12.0
 */

require_once 'PHPUnit/Runner/Version.php';

/**
 * @package    Stagehand_TestRunner
 * @copyright  2010-2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.17.0
 * @since      Class available since Release 2.12.0
 */
class Stagehand_TestRunner_Preparer_PHPUnitPreparer extends Stagehand_TestRunner_Preparer
{
    public function prepare()
    {
        $this->prepareFramework();
        if (!is_null($this->config->phpunitConfigFile)) {
            $this->earlyConfigure();
        }
    }

    /**
     * Loads a bootstrap file.
     *
     * @param  string  $filename
     * @param  boolean $syntaxCheck
     * @see PHPUnit_TextUI_Command::handleBootstrap()
     * @since Method available since Release 2.16.0
     */
    protected function handleBootstrap($filename, $syntaxCheck = false)
    {
        try {
            PHPUnit_Util_Fileloader::checkAndLoad($filename, $syntaxCheck);
        } catch (RuntimeException $e) {
            PHPUnit_TextUI_TestRunner::showError($e->getMessage());
        }
    }

    /**
     * @since Method available since Release 2.16.0
     */
    protected function prepareFramework()
    {
        if (version_compare(PHPUnit_Runner_Version::id(), '3.5.0beta1', '>=')) {
            require_once 'PHPUnit/Autoload.php';
        } else {
            require_once 'PHPUnit/Framework.php';
        }
    }

    /**
     * @since Method available since Release 2.16.0
     */
    protected function earlyConfigure()
    {
        require_once 'PHPUnit/Util/Configuration.php';
        $phpunitConfiguration = PHPUnit_Util_Configuration::getInstance($this->config->phpunitConfigFile)->getPHPUnitConfiguration();
        if (array_key_exists('bootstrap', $phpunitConfiguration)) {
            if (array_key_exists('syntaxCheck', $phpunitConfiguration)) {
                $this->handleBootstrap($phpunitConfiguration['bootstrap'], $phpunitConfiguration['syntaxCheck']);
            } else {
                $this->handleBootstrap($phpunitConfiguration['bootstrap']);
            }
        }

        $browsers = PHPUnit_Util_Configuration::getInstance($this->config->phpunitConfigFile)->getSeleniumBrowserConfiguration();
        if (count($browsers)) {
            require_once 'PHPUnit/Extensions/SeleniumTestCase.php';
            PHPUnit_Extensions_SeleniumTestCase::$browsers = $browsers;
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
