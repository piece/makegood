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
 * @version    Release: 2.11.1
 * @link       http://www.phpspec.org/
 * @since      File available since Release 2.0.0
 */

/**
 * A reporter for PHPSpec.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2007-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.1
 * @link       http://www.phpspec.org/
 * @since      Class available since Release 2.0.0
 */
class Stagehand_TestRunner_Runner_PHPSpecRunner_TextReporter extends PHPSpec_Runner_Reporter_Text
{
    protected $color;

    /**
     * @param PHPSpec_Runner_Result $result
     * @param boolean               $color
     */
    public function __construct(PHPSpec_Runner_Result $result, $color)
    {
        parent::__construct($result);
        $this->color = $color;
    }

    /**
     * @param string $symbol
     */
    public function outputStatus($symbol)
    {
        if ($this->color) {
            switch ($symbol) {
            case '.':
                $symbol = Stagehand_TestRunner_Coloring::green($symbol);
                break;
            case 'F':
                $symbol = Stagehand_TestRunner_Coloring::red($symbol);
                break;
            case 'E':
                $symbol = Stagehand_TestRunner_Coloring::magenta($symbol);
                break;
            case 'P':
                $symbol = Stagehand_TestRunner_Coloring::yellow($symbol);
                break;
            }
        }

        parent::outputStatus($symbol);
    }

    /**
     * @param boolean $specs
     */
    public function output($specs = false)
    {
        $output = preg_replace(array('/(\x0d|\x0a|\x0d\x0a){3,}/', '/^(  -)(.+)/m'),
                               array("\n\n", '$1 $2'),
                               $this->toString($specs)
                               );

        if ($this->color) {
            $failuresCount = $this->_result->countFailures();
            $deliberateFailuresCount = $this->_result->countDeliberateFailures();
            $errorsCount = $this->_result->countErrors();
            $exceptionsCount = $this->_result->countExceptions();
            $pendingsCount = $this->_result->countPending();

            if ($failuresCount + $deliberateFailuresCount + $errorsCount + $exceptionsCount + $pendingsCount == 0) {
                $colorLabel = 'green';
            } elseif ($pendingsCount && $failuresCount + $deliberateFailuresCount + $errorsCount + $exceptionsCount == 0) {
                $colorLabel = 'yellow';
            } else {
                $colorLabel = 'red';
            }

            $oldErrorReportingLevel = error_reporting(error_reporting() & ~E_STRICT);
            $output = preg_replace(array('/^(\d+ examples?.*)/m',
                                         '/^(  -)(.+)( \(ERROR|EXCEPTION\))/m',
                                         '/^(  -)(.+)( \(FAIL\))/m',
                                         '/^(  -)(.+)( \(DELIBERATEFAIL\))/m',
                                         '/^(  -)(.+)( \(PENDING\))/m',
                                         '/^(  -)(.+)/m',
                                         '/(\d+\)\s+)(.+ (?:ERROR|EXCEPTION)\s+.+)/',
                                         '/(\d+\)\s+)(.+ FAILED\s+.+)/',
                                         '/(\d+\)\s+)(.+ PENDING\s+.+)/',
                                         '/^((?:Errors|Exceptions):)/m',
                                         '/^(Failures:)/m',
                                         '/^(Pending:)/m'
                                         ),
                                   array(Stagehand_TestRunner_Coloring::$colorLabel('$1'),
                                         Stagehand_TestRunner_Coloring::magenta('$1$2$3'),
                                         Stagehand_TestRunner_Coloring::red('$1$2$3'),
                                         Stagehand_TestRunner_Coloring::red('$1$2$3'),
                                         Stagehand_TestRunner_Coloring::yellow('$1$2$3'),
                                         Stagehand_TestRunner_Coloring::green('$1$2$3'),
                                         '$1' . Stagehand_TestRunner_Coloring::magenta('$2'),
                                         '$1' . Stagehand_TestRunner_Coloring::red('$2'),
                                         '$1' . Stagehand_TestRunner_Coloring::yellow('$2'),
                                         Stagehand_TestRunner_Coloring::magenta('$1'),
                                         Stagehand_TestRunner_Coloring::red('$1'),
                                         Stagehand_TestRunner_Coloring::yellow('$1')
                                         ),
                                   Console_Color::escape($output)
                                   );
            error_reporting($oldErrorReportingLevel);
        }

        print $output;
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
