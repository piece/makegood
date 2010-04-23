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
 * @version    Release: 2.11.1
 * @link       http://www.phpunit.de/
 * @since      File available since Release 2.7.0
 */

require_once 'PHPUnit/Framework/TestSuite.php';

/**
 * @package    Stagehand_TestRunner
 * @copyright  2009-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.1
 * @link       http://www.phpunit.de/
 * @since      Class available since Release 2.7.0
 */
class Stagehand_TestRunner_Collector_PHPUnitCollector_MethodFilterTestSuite extends PHPUnit_Framework_TestSuite
{
    protected $config;

    /**
     * @param ReflectionClass             $theClass
     * @param Stagehand_TestRunner_Config $config
     */
    public function __construct(ReflectionClass $theClass, Stagehand_TestRunner_Config $config)
    {
        $this->config = $config;
        parent::__construct($theClass);
    }

    /**
     * @param PHPUnit_Framework_Test $test
     * @param array                  $groups
     */
    public function addTest(PHPUnit_Framework_Test $test, $groups = array())
    {
        if ($test instanceof PHPUnit_Framework_Warning
            && preg_match('/^No tests found in class/', $test->getMessage())
            ) {
            return;
        }

        parent::addTest($test, $groups);
    }

    /**
     * @param ReflectionClass  $class
     * @param ReflectionMethod $method
     * @param array            $names
     */
    protected function addTestMethod(ReflectionClass $class, ReflectionMethod $method, array &$names)
    {
        if ($this->config->inMethodsToBeTested($class->getName(), $method->getName())) {
            parent::addTestMethod($class, $method, $names);
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
