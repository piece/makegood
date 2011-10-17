<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2011 KUBO Atsuhiro <kubo@iteman.jp>,
 *               2011 Shigenobu Nishikawa <shishi.s.n@gmail.com>,
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
 * @copyright  2011 Shigenobu Nishikawa <shishi.s.n@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.20.0
 * @since      File available since Release 2.18.0
 */

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @copyright  2011 Shigenobu Nishikawa <shishi.s.n@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.20.0
 * @since      Class available since Release 2.18.0
 */
class Stagehand_TestRunner_Notification_Notifier
{
    const TITLE_PASSED = 'Test Passed';
    const TITLE_FAILED = 'Test Failed';
    const TITLE_STOPPED = 'Test Stopped';

    public static $ICON_PASSED;
    public static $ICON_FAILED;
    public static $ICON_STOPPED;

    public function __construct()
    {
        $growlIconDir = dirname(__FILE__);
        self::$ICON_PASSED = $growlIconDir . DIRECTORY_SEPARATOR . 'passed.png';
        self::$ICON_FAILED = $growlIconDir . DIRECTORY_SEPARATOR . 'failed.png';
        self::$ICON_STOPPED = $growlIconDir . DIRECTORY_SEPARATOR . 'stopped.png';
    }

    /**
     * @param Stagehand_TestRunner_Notification_Notification $notification
     */
    public function notifyResult(Stagehand_TestRunner_Notification_Notification $notification)
    {
        $this->executeNotifyCommand($this->buildNotifyCommand($notification));
    }

    /**
     * @return boolean
     */
    public function isWin()
    {
        return strtolower(substr($this->getPHPOS(), 0, strlen('win'))) == 'win';
    }

    /**
     * @return boolean
     */
    public function isDarwin()
    {
        return strtolower(substr($this->getPHPOS(), 0, strlen('darwin'))) == 'darwin';
    }

    /**
     * @return boolean
     */
    public function isLinux()
    {
        return strtolower(substr($this->getPHPOS(), 0, strlen('linux'))) == 'linux';
    }

    /**
     * @param Stagehand_TestRunner_Notification_Notification $result
     * @return string
     */
    protected function buildNotifyCommand(Stagehand_TestRunner_Notification_Notification $notification)
    {
        if ($notification->isPassed()) {
            $title = self::TITLE_PASSED;
            $icon = self::$ICON_PASSED;
        } elseif ($notification->isFailed()) {
            $title = self::TITLE_FAILED;
            $icon = self::$ICON_FAILED;
        } elseif ($notification->isStopped()) {
            $title = self::TITLE_STOPPED;
            $icon = self::$ICON_STOPPED;
        }

        if ($this->isWin()) {
            return sprintf(
                'growlnotify /t:%s /p:-2 /i:%s /a:Stagehand_TestRunner /r:%s,%s,%s /n:%s /silent:true %s',
                escapeshellarg($title),
                escapeshellarg($icon),
                escapeshellarg(self::TITLE_PASSED),
                escapeshellarg(self::TITLE_FAILED),
                escapeshellarg(self::TITLE_STOPPED),
                escapeshellarg($title),
                escapeshellarg($notification->getMessage())
            );
        } elseif ($this->isDarwin()) {
            return sprintf(
                'growlnotify --name %s --priority -2 --image %s --title %s --message %s',
                escapeshellarg($title),
                escapeshellarg($icon),
                escapeshellarg($title),
                escapeshellarg($notification->getMessage())
            );
        } elseif ($this->isLinux()) {
            return sprintf(
                'notify-send --urgency=low --icon=%s %s %s',
                escapeshellarg($icon),
                escapeshellarg($title),
                escapeshellarg(str_replace('\\', '\\\\', $notification->getMessage()))
            );
        }
    }

    /**
     * @param string $command
     */
    protected function executeNotifyCommand($command)
    {
        if (strlen($command) > 0) {
            system($command);
        }
    }

    /**
     * @return string
     */
    protected function getPHPOS()
    {
        return PHP_OS;
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
