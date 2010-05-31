<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2009-2010 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2009-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.2
 * @link       http://www.phpunit.de/
 * @since      File available since Release 2.6.2
 */

require_once 'PHPUnit/Util/TestDox/NamePrettifier.php';

/**
 * Prettifies class and method names for use in TestDox documentation.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2009-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.2
 * @link       http://www.phpunit.de/
 * @since      Class available since Release 2.6.2
 */
class Stagehand_TestRunner_Runner_PHPUnitRunner_TestDox_NamePrettifier extends PHPUnit_Util_TestDox_NamePrettifier
{
    /**
     * Prettifies the name of a test method.
     *
     * @param  string  $testMethodName
     * @return string
     */
    public function prettifyTestMethod($testMethodName)
    {
        $buffer = '';

        if (!is_string($testMethodName) || strlen($testMethodName) == 0) {
            return $buffer;
        }

        $max = strlen($testMethodName);

        if (substr($testMethodName, 0, 4) == 'test') {
            $offset = 4;
        } else {
            $offset = 0;
            $testMethodName[0] = strtoupper($testMethodName[0]);
        }

        $wasNumeric = FALSE;

        for ($i = $offset; $i < $max; $i++) {
            if ($i > $offset &&
                ord($testMethodName[$i]) >= 65 &&
                ord($testMethodName[$i]) <= 90) {
                $buffer .= ' ' . strtolower($testMethodName[$i]);
            } else {
                $isNumeric = is_numeric($testMethodName[$i]);

                if (!$wasNumeric && $isNumeric) {
                    $buffer .= ' ';
                    $wasNumeric = TRUE;
                }

                if ($wasNumeric && !$isNumeric) {
                    $wasNumeric = FALSE;
                }

                $buffer .= $testMethodName[$i];
            }
        }

        return $buffer;
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
