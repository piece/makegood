<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2008-2009 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @package    Stagehand_Autoload
 * @copyright  2008-2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.1.0
 * @since      File available since Release 0.1.0
 */

require_once 'Stagehand/Autoload/Exception.php';
require_once 'Stagehand/Autoload/Loader.php';

// {{{ Stagehand_Autoload

/**
 * A utility to register class loaders to the SPL autoload queue.
 *
 * @package    Stagehand_Autoload
 * @copyright  2008-2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.1.0
 * @since      File available since Release 0.1.0
 */
class Stagehand_Autoload
{

    // {{{ constants

    const LOADER_LEGACY = 'Stagehand_Autoload_Loader_LegacyLoader';
    const LOADER_NAMESPACE = 'Stagehand_Autoload_Loader_NamespaceLoader';

    // }}}
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

    private static $cache = array();

    /**#@-*/

    /**#@+
     * @access public
     */

    // }}}
    // {{{ register()

    /**
     * @param Stagehand_Autoload_Loader $loader
     * @since Method available since Release 0.2.0
     */
    public static function register(Stagehand_Autoload_Loader $loader)
    {
        if (function_exists('__autoload')) {
            spl_autoload_register('__autoload');
        }

        spl_autoload_register(array($loader, 'load'));
    }

    // }}}
    // {{{ getLoader()

    /**
     * @param string $class
     * @return Stagehand_Autoload_Loader
     * @throws Stagehand_Autoload_Exception
     * @since Method available since Release 0.2.0
     */
    public static function getLoader($class)
    {
        if (!class_exists($class, false)) {
            $file = str_replace('_', '/', $class) . '.php';
            include_once $file;
            if (!class_exists($class, false)) {
                throw new Stagehand_Autoload_Exception(
                    'Class ' .
                    $class .
                    ' was not present in ' .
                    $file .
                    ', (include_path="' .
                    get_include_path() .
                    '")'
                                                       );
            }
        }

        if (array_key_exists($class, self::$cache)) {
            return self::$cache[$class];
        }

        self::$cache[$class] = new $class();
        return self::$cache[$class];
    }

    // }}}
    // {{{ legacyLoader()

    /**
     * @return Stagehand_Autoload_Loader
     * @since Method available since Release 0.4.0
     */
    public static function legacyLoader()
    {
        return self::getLoader(self::LOADER_LEGACY);
    }

    // }}}
    // {{{ namespaceLoader()

    /**
     * @return Stagehand_Autoload_Loader
     * @since Method available since Release 0.4.0
     */
    public static function namespaceLoader()
    {
        return self::getLoader(self::LOADER_NAMESPACE);
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
 * coding: iso-8859-1
 * tab-width: 4
 * c-basic-offset: 4
 * c-hanging-comment-ender-p: nil
 * indent-tabs-mode: nil
 * End:
 */
