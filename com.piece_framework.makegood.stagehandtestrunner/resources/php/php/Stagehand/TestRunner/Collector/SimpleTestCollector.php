<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2007 Masahiko Sakamoto <msakamoto-sf@users.sourceforge.net>,
 *               2007-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2007 Masahiko Sakamoto <msakamoto-sf@users.sourceforge.net>
 * @copyright  2007-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.3.1
 * @link       http://simpletest.org/
 * @since      File available since Release 2.1.0
 */

namespace Stagehand\TestRunner\Collector;

/**
 * A test collector for SimpleTest.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2007 Masahiko Sakamoto <msakamoto-sf@users.sourceforge.net>
 * @copyright  2007-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.3.1
 * @link       http://simpletest.org/
 * @since      Class available since Release 2.1.0
 */
class SimpleTestCollector extends Collector
{
    protected $suiteClass = 'Stagehand\TestRunner\TestSuite\SimpleTestTestSuite';

    /**
     * @param string $testCase
     * @since Method available since Release 2.11.0
     */
    public function collectTestCase($testCase)
    {
        $test = new \ReflectionClass($testCase);
        if ($test->isAbstract()) {
            return;
        }

        $this->suite->add(new $testCase());
    }

    /**
     * Creates the test suite object.
     *
     * @param string $name
     * @return \TestSuite
     */
    protected function createTestSuite($name)
    {
        $suite = new $this->suiteClass($name);
        $suite->setTestTargetRepository($this->testTargetRepository);
        return $suite;
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
