<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2011-2013 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2011-2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\Core;

use Stagehand\TestRunner\Core\Plugin\PluginInterface;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      Class available since Release 3.0.0
 */
class TestTargetRepository
{
    /**
     * @var \Stagehand\TestRunner\Core\Plugin\PluginInterface
     * @since Property available since Release 3.6.0
     */
    protected $plugin;

    /**
     * @var array
     */
    protected $resources;

    /**
     * @var array
     */
    protected $methods;

    /**
     * @var array
     */
    protected $classes;

    /**
     * The pattern of test files by a regular expression literal.
     * The regular expression literal must not contain .php.
     *
     * @var string
     */
    protected $filePattern;

    /**
     * @param \Stagehand\TestRunner\Core\Plugin\PluginInterface $plugin
     * @since Method available since Release 3.6.0
     */
    public function __construct(PluginInterface $plugin)
    {
        $this->plugin = $plugin;
    }

    /**
     * @param array $resources
     */
    public function setResources(array $resources)
    {
        $this->resources = $resources;
    }

    /**
     * @return array
     */
    public function getResources()
    {
        return $this->resources;
    }

    /**
     * @param \Closure $callable
     */
    public function walkOnResources(\Closure $callable)
    {
        array_walk($this->resources, $callable, $this);
    }

    /**
     * @param array $methods
     */
    public function setMethods(array $methods)
    {
        $this->methods = array_map(function ($v) { return strtolower(ltrim(urldecode($v), '\\')); }, $methods);
    }

    /**
     * @return boolean
     */
    public function testsOnlySpecifiedMethods()
    {
        return count($this->methods);
    }

    /**
     * @param array $classes
     */
    public function setClasses(array $classes)
    {
        $this->classes = array_map(function ($v) { return strtolower(ltrim(urldecode($v), '\\')); }, $classes);
    }

    /**
     * @return boolean
     */
    public function testsOnlySpecifiedClasses()
    {
        return count($this->classes);
    }

    /**
     * @param string $class
     * @param string $method
     * @return boolean
     */
    public function shouldTreatElementAsTest($class, $method = null)
    {
        if ($this->testsOnlySpecifiedMethods() || $this->testsOnlySpecifiedClasses()) {
            if (is_null($method)) {
                return in_array(strtolower($class), $this->classes);
            } else {
                foreach (array($class . '::' . $method, $method) as $fullyQualifiedMethodName) {
                    if (in_array(strtolower($fullyQualifiedMethodName), $this->methods)) {
                        return true;
                    }
                }

                return false;
            }
        } else {
           return true;
        }
    }

    /**
     * @param string $filePattern
     */
    public function setFilePattern($filePattern)
    {
        $this->filePattern = $filePattern;
    }

    /**
     * @return string
     */
    public function getFilePattern()
    {
        return $this->filePattern;
    }

    /**
     * @return boolean
     */
    public function isDefaultFilePattern()
    {
        return $this->getFilePattern() == $this->plugin->getTestFilePattern();
    }

    /**
     * @param string $file
     * @return boolean
     */
    public function shouldTreatFileAsTest($file)
    {
        return (boolean)preg_match('/' . str_replace('/', '\/', $this->getFilePattern()) . '/', basename($file));
    }

    /**
     * @return array
     */
    public function getRequiredSuperTypes()
    {
        return $this->plugin->getTestClassSuperTypes();
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
