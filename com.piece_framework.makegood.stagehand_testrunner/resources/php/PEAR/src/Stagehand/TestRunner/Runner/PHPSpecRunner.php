<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2007-2010 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2007-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.2
 * @link       http://www.phpspec.org/
 * @since      File available since Release 2.1.0
 */

/**
 * A test runner for PHPSpec.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2007-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.2
 * @link       http://www.phpspec.org/
 * @since      Class available since Release 2.1.0
 */
class Stagehand_TestRunner_Runner_PHPSpecRunner extends Stagehand_TestRunner_Runner
{
    /**
     * Runs tests based on the given ArrayObject object.
     *
     * @param ArrayObject $suite
     */
    public function run($suite)
    {
        $result = new PHPSpec_Runner_Result();
        $reporter = new Stagehand_TestRunner_Runner_PHPSpecRunner_TextReporter(
                        $result,
                        $this->config->colors
                                                                           );
        $result->setReporter($reporter);

        $result->setRuntimeStart(microtime(true));
        foreach ($suite as $contextClass) {
            $collection = new PHPSpec_Runner_Collection(new $contextClass());
            PHPSpec_Runner_Base::execute($collection, $result);
        }
        $result->setRuntimeEnd(microtime(true));

        $reporter->output(true);

        if ($this->config->usesGrowl) {
            $output = $reporter->toString(true);

            $failuresCount = $result->countFailures();
            $deliberateFailuresCount = $result->countDeliberateFailures();
            $errorsCount = $result->countErrors();
            $exceptionsCount = $result->countExceptions();
            $pendingsCount = $result->countPending();

            $this->notification = new stdClass();
            if ($failuresCount + $deliberateFailuresCount + $errorsCount + $exceptionsCount + $pendingsCount == 0) {
                $this->notification->name = 'Green';
            } elseif ($pendingsCount && $failuresCount + $deliberateFailuresCount + $errorsCount + $exceptionsCount == 0) {
                $this->notification->name = 'Green';
            } else {
                $this->notification->name = 'Red';
            }

            preg_match('/^(\d+ examples?, \d+ failures?.*)/m', $output, $matches);
            $this->notification->description = $matches[1];
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
