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
 * @package    Stagehand_Autoload
 * @copyright  2009-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.1.0
 * @since      File available since Release 0.2.0
 */

// {{{ Stagehand_Autoload_Loader

/**
 * @package    Stagehand_Autoload
 * @copyright  2009-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.1.0
 * @since      File available since Release 0.2.0
 */
abstract class Stagehand_Autoload_Loader
{

    // {{{ properties

    /**#@+
     * @access public
     */

    /**#@-*/

    /**#@+
     * @access protected
     */

    protected $namespaceSeparator;
    protected $namespaces = array();

    /**#@-*/

    /**#@+
     * @access private
     */

    /**#@-*/

    /**#@+
     * @access public
     */

    // }}}
    // {{{ load()

    /**
     * Loads an appropriate class.
     *
     * @param string $class
     * @return boolean
     */
    public function load($class)
    {
        if (strpos($class, '.') !== false) {
            return false;
        }

        $class = $this->normalizeClassName($class);
        if (class_exists($class, false)) {
            return true;
        }

        if (!$this->inNamespaces($class)) {
            return false;
        }

        $file = str_replace($this->namespaceSeparator, '/', $class) . '.php';
        $oldErrorReportingLevel = error_reporting(error_reporting() & ~E_WARNING);
        $result = $this->loadFile($file);
        error_reporting($oldErrorReportingLevel);
        if ($result === false) {
            return false;
        }

        if (!class_exists($class, false) && !interface_exists($class, false)) {
            trigger_error(
                'Class ' .
                $class .
                ' was not present in ' .
                $file .
                ', (include_path="' .
                get_include_path() .
                '")',
                E_USER_WARNING
                          );
            return false;
        }

        return true;
    }

    // }}}
    // {{{ addNamespace()

    /**
     * Adds a namespace to the targets for autoloading.
     *
     * @param string $namespace
     */
    public function addNamespace($namespace)
    {
        if (in_array($namespace, $this->namespaces)) {
            return;
        }

        $this->namespaces[] = $namespace;
    }

    /**#@-*/

    /**#@+
     * @access protected
     */

    // }}}
    // {{{ loadFile()

    /**
     * @param string $file
     * @return boolean
     */
    protected function loadFile($file)
    {
        return include $file;
    }

    // }}}
    // {{{ normalizeClassName()

    /**
     * @param string $class
     * @return string
     * @since Method available since Release 0.5.0
     */
    protected function normalizeClassName($class)
    {
        return $class;
    }

    // }}}
    // {{{ inNamespaces()

    /**
     * @param string $class
     * @return boolean
     * @since Method available since Release 0.5.0
     */
    protected function inNamespaces($class)
    {
        foreach ($this->namespaces as $namespace) {
            if ($this->matchNamespace($class, $namespace)) {
                return true;
            }
        }

        return false;
    }

    // }}}
    // {{{ matchNamespace()

    /**
     * @param string $class
     * @param string $namespace
     * @return boolean
     * @since Method available since Release 0.5.0
     */
    protected function matchNamespace($class, $namespace)
    {
        return (boolean)preg_match('/^' . preg_quote($namespace . $this->namespaceSeparator) . '/', $class);
    }

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
