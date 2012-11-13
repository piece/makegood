<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2011-2012 KUBO Atsuhiro <kubo@iteman.jp>,
 *               2012 tsyk goto <ngyuki.ts@gmail.com>,
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
 * @copyright  2012 tsyk goto <ngyuki.ts@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.4.0
 * @link       http://www.phpunit.de/
 * @since      File available since Release 2.17.0
 */

namespace Stagehand\TestRunner\TestSuite;

use Stagehand\TestRunner\Util\PHPUnitXMLConfiguration;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @copyright  2012 tsyk goto <ngyuki.ts@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.4.0
 * @link       http://www.phpunit.de/
 * @since      Class available since Release 2.17.0
 */
class PHPUnitGroupFilterTestSuite extends \PHPUnit_Framework_TestSuite
{
    /**
     * @var \Stagehand\TestRunner\Util\PHPUnitXMLConfiguration
     * @since Property available since Release 3.0.0
     */
    protected $phpunitXMLConfiguration;

    /**
     * @param \ReflectionClass $theClass
     * @param \Stagehand\TestRunner\Util\PHPUnitXMLConfiguration $phpunitXMLConfiguration
     */
    public function __construct(\ReflectionClass $theClass, PHPUnitXMLConfiguration $phpunitXMLConfiguration = null)
    {
        $this->phpunitXMLConfiguration = $phpunitXMLConfiguration;
        parent::__construct($theClass);

        if ($this->phpunitXMLConfiguration->isEnabled()) {
            $this->filterGroup();
        }
    }

    /**
     * @since Method available since Release 3.4.0
     */
    protected function filterGroup()
    {
        $include = null;
        $exclude = null;

        if ($this->phpunitXMLConfiguration->hasGroupConfiguration('include')) {
            $include = $this->phpunitXMLConfiguration->getGroupConfiguration('include');
        }

        if ($this->phpunitXMLConfiguration->hasGroupConfiguration('exclude')) {
            $exclude = $this->phpunitXMLConfiguration->getGroupConfiguration('exclude');

            if (is_array($include)) {
                $include = array_diff($include, $exclude);
            }
        }

        $groups = null;
        $filter = null;

        if (is_array($include)) {
            $groups = $include;
            $filter = true;
        } else if (is_array($exclude)) {
            $groups = $exclude;
            $filter = false;
        }

        if ($groups !== null) {
            $objects = new \SplObjectStorage();

            foreach ($groups as $group) {
                if (isset($this->groups[$group])) {
                    foreach($this->groups[$group] as $test) {
                        $objects->attach($test);
                    }
                }
            }

            $this->tests = array_filter($this->tests, function($test) use ($objects, $filter) {
                if ($objects->contains($test)) {
                    return $filter;
                } else {
                    return !$filter;
                }
            });
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
