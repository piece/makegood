<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2011-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.1.0
 * @link       http://www.phpunit.de/
 * @since      File available since Release 2.17.0
 */

namespace Stagehand\TestRunner\TestSuite;

use Stagehand\TestRunner\Core\PHPUnitXMLConfiguration;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.1.0
 * @link       http://www.phpunit.de/
 * @since      Class available since Release 2.17.0
 */
class PHPUnitGroupFilterTestSuite extends \PHPUnit_Framework_TestSuite
{
    protected $excluded = false;

    /**
     * @var \Stagehand\TestRunner\Core\PHPUnitXMLConfiguration
     * @since Property available since Release 3.0.0
     */
    protected $phpunitXMLConfiguration;

    /**
     * @param \ReflectionClass $theClass
     * @param \Stagehand\TestRunner\Core\PHPUnitXMLConfiguration $phpunitXMLConfiguration
     */
    public function __construct(\ReflectionClass $theClass, PHPUnitXMLConfiguration $phpunitXMLConfiguration = null)
    {
        $this->phpunitXMLConfiguration = $phpunitXMLConfiguration;
        parent::__construct($theClass);
        if (count($this->tests) == 1 && $this->tests[0]) {
            if ($this->tests[0] instanceof \PHPUnit_Framework_Warning
                && preg_match('/^No tests found in class/', $this->tests[0]->getMessage())
            ) {
                if ($this->excluded) {
                    unset($this->tests[0]);
                }
            }
        }
    }

    /**
     * @param \ReflectionClass $theClass
     * @return boolean
     */
    protected function shouldExclude(\ReflectionClass $class, \ReflectionMethod $method)
    {
        if (!$this->phpunitXMLConfiguration->isEnabled()) return false;

        $groups = \PHPUnit_Util_Test::getGroups($class->getName(), $method->getName());
        $shouldExclude = false;
        if ($this->phpunitXMLConfiguration->hasGroupConfiguration('include')) {
            $groupConfiguration = $this->phpunitXMLConfiguration->getGroupConfiguration('include');
            $shouldExclude = true;
            foreach ($groups as $group) {
                if (in_array($group, $groupConfiguration)) {
                    $shouldExclude = false;
                    break;
                }
            }
        }

        if ($this->phpunitXMLConfiguration->hasGroupConfiguration('exclude')) {
            $groupConfiguration = $this->phpunitXMLConfiguration->getGroupConfiguration('exclude');
            foreach ($groups as $group) {
                if (in_array($group, $groupConfiguration)) {
                    $shouldExclude = true;
                    break;
                }
            }
        }

        return $shouldExclude;
    }

    protected function markAsExcluded()
    {
        $this->excluded = true;
    }

    /**
     * @since Method available since Release 3.0.0
     */
    protected function addTestMethod(\ReflectionClass $class, \ReflectionMethod $method)
    {
        if ($this->shouldExclude($class, $method)) {
            $this->markAsExcluded();
            return;
        }

        parent::addTestMethod($class, $method);
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
