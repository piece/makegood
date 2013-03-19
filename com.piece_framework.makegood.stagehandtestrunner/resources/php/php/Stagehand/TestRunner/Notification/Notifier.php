<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2011-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @copyright  2011 Shigenobu Nishikawa <shishi.s.n@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.0
 * @since      File available since Release 2.18.0
 */

namespace Stagehand\TestRunner\Notification;

use Stagehand\TestRunner\Util\LegacyProxy;
use Stagehand\TestRunner\Util\OS;

Notifier::$ICON_PASSED = __DIR__ . DIRECTORY_SEPARATOR . '..' . DIRECTORY_SEPARATOR . 'Resources' . DIRECTORY_SEPARATOR . 'icons' . DIRECTORY_SEPARATOR . 'passed.png';
Notifier::$ICON_FAILED = __DIR__ . DIRECTORY_SEPARATOR . '..' . DIRECTORY_SEPARATOR . 'Resources' . DIRECTORY_SEPARATOR . 'icons' . DIRECTORY_SEPARATOR . 'failed.png';
Notifier::$ICON_STOPPED = __DIR__ . DIRECTORY_SEPARATOR . '..' . DIRECTORY_SEPARATOR . 'Resources' . DIRECTORY_SEPARATOR . 'icons' . DIRECTORY_SEPARATOR . 'stopped.png';

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @copyright  2011 Shigenobu Nishikawa <shishi.s.n@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.0
 * @since      Class available since Release 2.18.0
 */
class Notifier
{
    const TITLE_PASSED = 'Test Passed';
    const TITLE_FAILED = 'Test Failed';
    const TITLE_STOPPED = 'Test Stopped';

    public static $ICON_PASSED;
    public static $ICON_FAILED;
    public static $ICON_STOPPED;

    /**
     * @var \Stagehand\TestRunner\Util\LegacyProxy
     * @since Property available since Release 3.0.0
     */
    protected $legacyProxy;

    /**
     * @var \Stagehand\TestRunner\Util\OS
     * @since Property available since Release 3.0.1
     */
    protected $os;

    /**
     * @param \Stagehand\TestRunner\Util\OS $os
     * @since Method available since Release 3.0.1
     */
    public function setOS(OS $os)
    {
        $this->os = $os;
    }

    /**
     * @param \Stagehand\TestRunner\Notification\Notification $notification
     */
    public function notifyResult(Notification $notification)
    {
        $this->executeNotifyCommand($this->buildNotifyCommand($notification));
    }

    /**
     * @param \Stagehand\TestRunner\Util\LegacyProxy $legacyProxy
     * @since Method available since Release 3.0.0
     */
    public function setLegacyProxy(LegacyProxy $legacyProxy)
    {
        $this->legacyProxy = $legacyProxy;
    }

    /**
     * @param \Stagehand\TestRunner\Notification\Notification $notification
     * @return string
     */
    protected function buildNotifyCommand(Notification $notification)
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

        if ($this->os->isWin()) {
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
        } elseif ($this->os->isDarwin()) {
            return sprintf(
                'growlnotify --name %s --priority -2 --image %s --title %s --message %s',
                escapeshellarg($title),
                escapeshellarg($icon),
                escapeshellarg($title),
                escapeshellarg($notification->getMessage())
            );
        } elseif ($this->os->isLinux()) {
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
            $exitStatus = null;
            $this->legacyProxy->system($command, $exitStatus);
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
