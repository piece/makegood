<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2007-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2007-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.0.3
 * @link       http://www.phpunit.de/
 * @since      File available since Release 2.1.0
 */

namespace Stagehand\TestRunner\Collector;

use Stagehand\TestRunner\Core\PHPUnitXMLConfiguration;
use Stagehand\TestRunner\TestSuite\PHPUnitGroupFilterTestSuite;
use Stagehand\TestRunner\TestSuite\PHPUnitMethodFilterTestSuite;

/**
 * A test collector for PHPUnit.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2007-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.0.2
 * @link       http://www.phpunit.de/
 * @since      Class available since Release 2.1.0
 */
class PHPUnitCollector extends Collector
{
    /**
     * @var integer
     * @since Property available since Release 3.0.3
     */
    private static $FILTER_CLASS = 1;

    /**
     * @var integer
     * @since Property available since Release 3.0.3
     */
    private static $FILTER_METHOD = 2;

    /**
     * @var \Stagehand\TestRunner\Core\PHPUnitXMLConfiguration
     * @since Property available since Release 3.0.0
     */
    protected $phpunitXMLConfiguration;

    /**
     * @param string $testCase
     * @since Method available since Release 2.11.0
     */
    public function collectTestCase($testCase)
    {
        $testClass = new \ReflectionClass($testCase);
        if ($testClass->isAbstract()) return;

        $suiteMethod = $this->findSuiteMethod($testClass);
        if (is_null($suiteMethod)) {
            $testSuite = new PHPUnitGroupFilterTestSuite($testClass, $this->phpunitXMLConfiguration);
        } else {
            $testSuite = $suiteMethod->invoke(null, $testClass->getName());
        }

        if ($this->testTargets->testsOnlySpecifiedMethods()) {
            $this->filterTests($testSuite, self::$FILTER_METHOD);
        } elseif ($this->testTargets->testsOnlySpecifiedClasses()) {
            $this->filterTests($testSuite, self::$FILTER_CLASS);
        }

        if ($testSuite->count() > 0) {
            $this->suite->addTest($testSuite);
        }
    }

    /**
     * @param \Stagehand\TestRunner\Core\PHPUnitXMLConfiguration $phpunitXMLConfiguration
     * @since Method available since Release 3.0.0
     */
    public function setPHPUnitXMLConfiguration(PHPUnitXMLConfiguration $phpunitXMLConfiguration = null)
    {
        $this->phpunitXMLConfiguration = $phpunitXMLConfiguration;
    }

    /**
     * Creates the test suite object.
     *
     * @param string $name
     * @return \PHPUnit_Framework_TestSuite
     */
    protected function createTestSuite($name)
    {
        return new \PHPUnit_Framework_TestSuite($name);
    }

    /**
     * @param \ReflectionClass $testClass
     * @return \ReflectionMethod
     * @since Method available since Release 3.0.3
     */
    protected function findSuiteMethod(\ReflectionClass $testClass)
    {
        if ($testClass->hasMethod(\PHPUnit_Runner_BaseTestRunner::SUITE_METHODNAME)) {
            $method = $testClass->getMethod(\PHPUnit_Runner_BaseTestRunner::SUITE_METHODNAME);
            if ($method->isStatic()) {
                return $method;
            }
        }
    }

    /**
     * @param \PHPUnit_Framework_TestSuite $testSuite
     * @param integer $filter
     * @since Method available since Release 3.0.3
     */
    protected function filterTests(\PHPUnit_Framework_TestSuite $testSuite, $filter)
    {
        $filteredTests = array();
        foreach ($testSuite as $test) {
            if ($test instanceof \PHPUnit_Framework_TestCase) {
                $testClassName = get_class($test);
                $testMethodName = $test->getName(false);
                if ($this->testTargets->shouldTreatElementAsTest($testClassName, $filter == self::$FILTER_METHOD ? $testMethodName : null)) {
                    $filteredTests[] = $test;
                }
            } else {
                $this->filterTests($test, $filter);
            }
        }

        $testSuiteClass = new \ReflectionClass($testSuite);
        $testsProperty = $testSuiteClass->getProperty('tests');
        $testsProperty->setAccessible(true);
        $testsProperty->setValue($testSuite, $filteredTests);
        $testsProperty->setAccessible(false);
        $numTestsProperty = $testSuiteClass->getProperty('numTests');
        $numTestsProperty->setAccessible(true);
        $numTestsProperty->setValue($testSuite, -1);
        $numTestsProperty->setAccessible(false);
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
