<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2010-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2010-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.4.0
 * @since      File available since Release 2.12.0
 */

namespace Stagehand\TestRunner\Preparer;

require_once 'PHPUnit/Autoload.php';

use Stagehand\TestRunner\CLI\Terminal;
use Stagehand\TestRunner\Util\PHPUnitXMLConfiguration;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2010-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.4.0
 * @since      Class available since Release 2.12.0
 */
class PHPUnitPreparer extends Preparer
{
    /**
     * @var \Stagehand\TestRunner\Util\PHPUnitXMLConfiguration
     * @since Property available since Release 3.0.0
     */
    protected $phpunitXMLConfiguration;

    /**
     * @var \Stagehand\TestRunner\CLI\Terminal
     * @since Property available since Release 3.0.0
     */
    protected $terminal;

    public function prepare()
    {
        if ($this->phpunitXMLConfiguration->isEnabled()) {
            $this->earlyConfigure();
        }
    }

    /**
     * @param \Stagehand\TestRunner\Util\PHPUnitXMLConfiguration $phpunitXMLConfiguration
     * @since Method available since Release 3.0.0
     */
    public function setPHPUnitXMLConfiguration(PHPUnitXMLConfiguration $phpunitXMLConfiguration = null)
    {
        $this->phpunitXMLConfiguration = $phpunitXMLConfiguration;
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
     * Loads a bootstrap file.
     *
     * @param  string  $filename
     * @param  boolean $syntaxCheck
     * @see \PHPUnit_TextUI_Command::handleBootstrap()
     * @since Method available since Release 2.16.0
     */
    protected function handleBootstrap($filename, $syntaxCheck = false)
    {
        try {
            \PHPUnit_Util_Fileloader::checkAndLoad($filename, $syntaxCheck);
        } catch (RuntimeException $e) {
            \PHPUnit_TextUI_TestRunner::showError($e->getMessage());
        }
    }

    /**
     * @since Method available since Release 2.16.0
     */
    protected function earlyConfigure()
    {
        $this->phpunitXMLConfiguration->handlePHPConfiguration();

        if ($this->phpunitXMLConfiguration->hasPHPUnitConfiguration('bootstrap')) {
            if ($this->phpunitXMLConfiguration->hasPHPUnitConfiguration('syntaxCheck')) {
                $this->handleBootstrap(
                    $this->phpunitXMLConfiguration->getPHPUnitConfiguration('bootstrap'),
                    $this->phpunitXMLConfiguration->getPHPUnitConfiguration('syntaxCheck')
                );
            } else {
                $this->handleBootstrap($this->phpunitXMLConfiguration->getPHPUnitConfiguration('bootstrap'));
            }
        }

        if ($this->phpunitXMLConfiguration->hasPHPUnitConfiguration('colors')) {
            $this->terminal->setColor($this->phpunitXMLConfiguration->getPHPUnitConfiguration('colors'));
        }

        if ($this->phpunitXMLConfiguration->hasSeleniumBrowserConfiguration()) {
            \PHPUnit_Extensions_SeleniumTestCase::$browsers =
                $this->phpunitXMLConfiguration->getSeleniumBrowserConfiguration();
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
