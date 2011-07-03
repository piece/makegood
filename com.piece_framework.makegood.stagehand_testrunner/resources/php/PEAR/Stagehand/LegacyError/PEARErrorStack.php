<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2009, 2011 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @package    Stagehand_LegacyError
 * @copyright  2009, 2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.0.1
 * @since      File available since Release 0.1.0
 */

$Stagehand_LegacyError_PEARErrorStack_oldErrorReportingLevel = error_reporting(error_reporting() & ~E_STRICT);
require_once 'PEAR/ErrorStack.php';
error_reporting($Stagehand_LegacyError_PEARErrorStack_oldErrorReportingLevel);
unset($Stagehand_LegacyError_PEARErrorStack_oldErrorReportingLevel);

// {{{ Stagehand_LegacyError_PEARErrorStack

/**
 * @package    Stagehand_LegacyError
 * @copyright  2009, 2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.0.1
 * @since      Class available since Release 0.1.0
 */
class Stagehand_LegacyError_PEARErrorStack
{

    // {{{ properties

    /**#@+
     * @access public
     */

    /**#@-*/

    /**#@+
     * @access protected
     */

    /**#@-*/

    /**#@+
     * @access private
     */

    private static $oldCallback;

    /**#@-*/

    /**#@+
     * @access public
     */

    // }}}
    // {{{ toException()

    /**
     * @param array $error
     * @throws Stagehand_LegacyError_Exceptionxion
     */
    public static function toException(array $error)
    {
        throw new Stagehand_LegacyError_PEARErrorStack_Exception($error);
    }

    // }}}
    // {{{ enableConversion()

    /**
     */
    public static function enableConversion()
    {
        self::$oldCallback = $GLOBALS['_PEAR_ERRORSTACK_DEFAULT_CALLBACK']['*'];
        $GLOBALS['_PEAR_ERRORSTACK_DEFAULT_CALLBACK']['*'] =
            array(__CLASS__, 'toException');
        class_exists('Stagehand_LegacyError_PEARErrorStack_Exception');
    }

    // }}}
    // {{{ disableConversion()

    /**
     */
    public static function disableConversion()
    {
        $GLOBALS['_PEAR_ERRORSTACK_DEFAULT_CALLBACK']['*'] = self::$oldCallback;
    }

    /**#@-*/

    /**#@+
     * @access protected
     */

    /**#@-*/

    /**#@+
     * @access private
     */

    /**#@-*/

    // }}}
}

// }}}

/*
 * Local Variables:
 * mode: php
 * coding: utf-8
 * tab-width: 4
 * c-basic-offset: 4
 * c-hanging-comment-ender-p: nil
 * indent-tabs-mode: nil
 * End:
 */
