<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2007-2011 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2007-2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.20.0
 * @link       http://www.phpunit.de/
 * @since      File available since Release 2.1.0
 */

require_once 'PHPUnit/Runner/BaseTestRunner.php';

/**
 * A test collector for PHPUnit.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2007-2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.20.0
 * @link       http://www.phpunit.de/
 * @since      Class available since Release 2.1.0
 */
class Stagehand_TestRunner_Collector_PHPUnitCollector extends Stagehand_TestRunner_Collector
{
    protected $superTypes = array('PHPUnit_Framework_TestCase');
    protected $filePattern = 'Test(?:Case)?\.php$';

    /**
     * @param string $testCase
     * @since Method available since Release 2.11.0
     */
    public function collectTestCase($testCase)
    {
        $testClass = new ReflectionClass($testCase);
        if ($testClass->isAbstract()) {
            return;
        }

        if ($this->config->testsOnlySpecified()) {
            $this->addTestCaseOnlySpecified($testClass);
            return;
        }

        $suiteMethod = false;
        if ($testClass->hasMethod(PHPUnit_Runner_BaseTestRunner::SUITE_METHODNAME)) {
            $method = $testClass->getMethod(PHPUnit_Runner_BaseTestRunner::SUITE_METHODNAME);
            if ($method->isStatic()) {
                $this->suite->addTest($method->invoke(null, $testClass->getName()));
                $suiteMethod = true;
            }
        }

        if (!$suiteMethod) {
            $this->suite->addTest(
                version_compare(PHPUnit_Runner_Version::id(), '3.5.0RC1', '>=')
                    ? new Stagehand_TestRunner_TestSuite_PHPUnit35GroupFilterTestSuite($testClass, $this->config)
                    : new Stagehand_TestRunner_TestSuite_PHPUnit34GroupFilterTestSuite($testClass, $this->config)
            );
        }
    }

    /**
     * Creates the test suite object.
     *
     * @param string $name
     * @return PHPUnit_Framework_TestSuite
     */
    protected function createTestSuite($name)
    {
        return new PHPUnit_Framework_TestSuite($name);
    }

    /**
     * @param ReflectionClass $testClass
     * @since Method available since Release 2.10.0
     */
    protected function addTestCaseOnlySpecified(ReflectionClass $testClass)
    {
        if ($this->config->testsOnlySpecifiedMethods) {
            $this->suite->addTestSuite(
                version_compare(PHPUnit_Runner_Version::id(), '3.5.0RC1', '>=')
                    ? new Stagehand_TestRunner_TestSuite_PHPUnit35MethodFilterTestSuite($testClass, $this->config)
                    : new Stagehand_TestRunner_TestSuite_PHPUnit34MethodFilterTestSuite($testClass, $this->config)
            );
        } elseif ($this->config->testsOnlySpecifiedClasses) {
            if ($this->config->isTestingClass($testClass->getName())) {
                $this->suite->addTestSuite($testClass);
            }
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
