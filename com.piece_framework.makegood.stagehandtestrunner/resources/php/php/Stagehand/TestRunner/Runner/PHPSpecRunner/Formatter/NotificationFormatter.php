<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.5.0
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\Runner\PHPSpecRunner\Formatter;

use PHPSpec\Runner\Formatter\Progress;

use Stagehand\TestRunner\Notification\Notification;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.5.0
 * @since      Class available since Release 3.0.0
 */
class NotificationFormatter extends Progress
{
    /**
     * @var \Stagehand\TestRunner\Notification\Notification
     */
    protected $notification;

    public function update(\SplSubject $subject, $reporterEvent = null)
    {
        if ($reporterEvent->event == 'exit') {
            parent::update($subject, $reporterEvent);
        }
    }

    public function output()
    {
        if (($this->_reporter->getFailures()->count() +
             $this->_reporter->getErrors()->count() +
             $this->_reporter->getPendingExamples()->count() +
             $this->_reporter->getExceptions()->count()) == 0) {
            $notificationResult = Notification::RESULT_PASSED;
        } else {
            $notificationResult = Notification::RESULT_FAILED;
        }

        $oldShowColors = $this->showColors();
        $this->setShowColors(false);
        $this->notification = new Notification($notificationResult, $this->getTotals());
        $this->setShowColors($oldShowColors);
    }

    /**
     * @return \Stagehand\TestRunner\Notification\Notification
     */
    public function getNotification()
    {
        return $this->notification;
    }

    protected function _onExit()
    {
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
