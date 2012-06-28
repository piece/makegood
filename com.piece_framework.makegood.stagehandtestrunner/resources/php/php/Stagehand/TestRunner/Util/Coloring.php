<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2008-2011 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2008-2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.1.0
 * @since      File available since Release 2.4.0
 */

namespace Stagehand\TestRunner\Util;

use Symfony\Component\Console\Formatter\OutputFormatterStyle;

/**
 * A utility for coloring.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2008-2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.1.0
 * @since      Class available since Release 2.4.0
 */
class Coloring
{
    /**
     * @var array
     * @since Property available since Release 3.0.0
     */
    private static $outputFormatterStyles = array();

    /**
     * @param string $text
     * @return string
     */
    public static function green($text)
    {
        return self::apply($text, __FUNCTION__);
    }

    /**
     * @param string $text
     * @return string
     */
    public static function red($text)
    {
        return self::apply($text, __FUNCTION__);
    }

    /**
     * @param string $text
     * @return string
     */
    public static function magenta($text)
    {
        return self::apply($text, __FUNCTION__);
    }

    /**
     * @param string $text
     * @return string
     */
    public static function yellow($text)
    {
        return self::apply($text, __FUNCTION__);
    }

    /**
     * @param string $text
     * @param string $color
     * @return string
     */
    private static function apply($text, $color)
    {
        if (!array_key_exists($color, self::$outputFormatterStyles)) {
            self::$outputFormatterStyles[$color] = new OutputFormatterStyle($color);
        }

        return self::$outputFormatterStyles[$color]->apply($text);
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
