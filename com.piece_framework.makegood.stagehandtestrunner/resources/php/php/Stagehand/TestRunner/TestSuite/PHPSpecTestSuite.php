<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\TestSuite;

use PHPSpec\Specification\ExampleGroup;
use PHPSpec\Util\Filter;

use Stagehand\TestRunner\Core\TestTargetRepository;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      Class available since Release 3.0.0
 */
class PHPSpecTestSuite
{
    /**
     * @var string
     */
    protected $name;

    /**
     * @var \Stagehand\TestRunner\Core\TestTargetRepository
     */
    protected $testTargetRepository;

    /**
     * @param array
     */
    protected $exampleGroups = array();

    /**
     * @param array
     */
    protected $examplesByGroup = array();

    /**
     * @param string $name
     * @param \Stagehand\TestRunner\Core\TestTargetRepository $testTargetRepository
     */
    public function __construct($name, TestTargetRepository $testTargetRepository)
    {
        $this->name = $name;
        $this->testTargetRepository = $testTargetRepository;
    }

    /**
     * @return string
     */
    public function getName()
    {
        return $this->name;
    }

    /**
     * @param \ReflectionClass $exampleGroupClass
     */
    public function addExampleGroup(\ReflectionClass $exampleGroupClass)
    {
        if ($this->testTargetRepository->testsOnlySpecifiedClasses()) {
            if (!$this->testTargetRepository->shouldTreatElementAsTest($exampleGroupClass->getName())) {
                return;
            }
        }

        $examples = array();
        foreach ($exampleGroupClass->getMethods(\ReflectionMethod::IS_PUBLIC) as $exampleMethod) { /* @var $exampleMethod \ReflectionMethod */
            if (strtolower(substr($exampleMethod->getName(), 0, 2)) == 'it') {
                if ($this->testTargetRepository->testsOnlySpecifiedMethods()) {
                    if (!$this->testTargetRepository->shouldTreatElementAsTest($exampleGroupClass->getName(), $exampleMethod->getName())) {
                        continue;
                    }
                }

                $examples[] = $exampleMethod;
            }
        }

        if (count($examples) > 0) {
            $exampleGroup = $exampleGroupClass->newInstance();
            $this->exampleGroups[] = $exampleGroup;
            $this->examplesByGroup[ get_class($exampleGroup) ] = $examples;
        }
    }

    /**
     * @return array
     */
    public function getExampleGroups()
    {
        return $this->exampleGroups;
    }

    /**
     * @return integer
     */
    public function getAllExampleCount()
    {
        $exampleCount = 0;
        foreach (array_values($this->examplesByGroup) as $exampleMethods) {
            $exampleCount += count($exampleMethods);
        }
        return $exampleCount;
    }

    /**
     * @param string $exampleGroupName
     * @return string
     * @throws \UnexpectedValueException
     */
    public function getExampleGroupClass($exampleGroupName)
    {
        $positionOfLastBackslash = strrpos($exampleGroupName, '\\');
        $exampleGroupClass = $positionOfLastBackslash === false
            ? ('Describe' . $exampleGroupName)
            : substr_replace($exampleGroupName, 'Describe', $positionOfLastBackslash + 1, 0);
        if (array_key_exists($exampleGroupClass, $this->examplesByGroup)) {
            return $exampleGroupClass;
        } else {
            throw new \UnexpectedValueException(sprintf('The example group corresponding to [ %s ] is not found in the test suite.', $exampleGroupName));
        }
    }

    /**
     * @param string $exampleGroupName
     * @return integer
     */
    public function getExampleCount($exampleGroupName)
    {
        return count($this->examplesByGroup[ $this->getExampleGroupClass($exampleGroupName) ]);
    }

    /**
     * @param string $exampleGroupName
     * @param string $specificationText
     * @return string
     * @throws \UnexpectedValueException
     */
    public function getExampleMethod($exampleGroupName, $specificationText)
    {
        foreach ($this->examplesByGroup[ $this->getExampleGroupClass($exampleGroupName) ] as $exampleMethod) { /* @var $exampleMethod \ReflectionMethod */
            if ($specificationText == Filter::camelCaseToSpace(substr($exampleMethod->getName(), 2))) {
                return $exampleMethod->getName();
            }
        }

        throw new \UnexpectedValueException(sprintf(
            'The example corresponding to [ %s ] and [ %s ] is not found in the test suite.',
            $specificationText,
            $exampleGroupName
        ));
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
