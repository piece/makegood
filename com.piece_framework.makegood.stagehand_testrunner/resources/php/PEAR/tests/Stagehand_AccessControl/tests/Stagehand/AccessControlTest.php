<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2009 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @package    Stagehand_AccessControl
 * @copyright  2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 0.1.0
 * @since      File available since Release 0.1.0
 */

// {{{ Stagehand_AccessControlOnOrderDenyAllowTest

/**
 * @package    Stagehand_AccessControl
 * @copyright  2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 0.1.0
 * @since      Class available since Release 0.1.0
 */
class Stagehand_AccessControlOnOrderDenyAllowTest extends Stagehand_AccessControlTest
{

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

    /**#@-*/

    /**#@+
     * @access public
     */

    public function setUp()
    {
        $this->control = Stagehand_AccessControl::denyAllow();
    }

    /**
     * @test
     */
    public function allowTheTargetByDefault()
    {
        $this->assertTrue($this->control->evaluate('192.168.0.1'));
    }

    /**
     * @test
     */
    public function denyTheTargetMatchedWithADenyRule()
    {
        $this->control->deny('^192\.168\.0\.1$');

        $this->assertFalse($this->control->evaluate('192.168.0.1'));
    }

    /**
     * @test
     */
    public function allowTheTargetMatchedWithAnAllowRule()
    {
        $this->control->allow('^192\.168\.0\.1$');

        $this->assertTrue($this->control->evaluate('192.168.0.1'));
    }

    /**
     * @test
     */
    public function allowTheTargetMatchedWithAnAllowRuleEvenThoughTheTargetMatchADenyRule()
    {
        $this->control->deny('^192\.168\.0\.1$');
        $this->control->allow('^192\.168\.0\.1$');

        $this->assertTrue($this->control->evaluate('192.168.0.1'));
    }

    /**
     * @test
     */
    public function setAnyMatcher()
    {
        $this->control->setMatcher(new Stagehand_AccessControl_Matcher_EqualMatcher());
        $this->control->deny('^192\.168\.0\.1$');

        $this->assertTrue($this->control->evaluate('192.168.0.1'));
        $this->assertFalse($this->control->evaluate('^192\.168\.0\.1$'));
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
// {{{ Stagehand_AccessControlOnOrderAllowDenyTest

/**
 * @package    Stagehand_AccessControl
 * @copyright  2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 0.1.0
 * @since      Class available since Release 0.1.0
 */
class Stagehand_AccessControlOnOrderAllowDenyTest extends Stagehand_AccessControlTest
{

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

    /**#@-*/

    /**#@+
     * @access public
     */

    public function setUp()
    {
        $this->control = Stagehand_AccessControl::allowDeny();
    }

    /**
     * @test
     */
    public function denyTheTargetByDefault()
    {
        $this->assertFalse($this->control->evaluate('192.168.0.1'));
    }

    /**
     * @test
     */
    public function allowTheTargetMatchedWithAnAllowRule()
    {
        $this->control->allow('^192\.168\.0\.1$');

        $this->assertTrue($this->control->evaluate('192.168.0.1'));
    }

    /**
     * @test
     */
    public function denyTheTargetMatchedWithADenyRule()
    {
        $this->control->deny('^192\.168\.0\.1$');

        $this->assertFalse($this->control->evaluate('192.168.0.1'));
    }

    /**
     * @test
     */
    public function denyTheTargetMatchedWithADenyRuleEvenThoughTheTargetMatchAnAllowRule()
    {
        $this->control->allow('^192\.168\.0\.1$');
        $this->control->deny('^192\.168\.0\.1$');

        $this->assertFalse($this->control->evaluate('192.168.0.1'));
    }

    /**
     * @test
     */
    public function setAnyMatcher()
    {
        $this->control->setMatcher(new Stagehand_AccessControl_Matcher_EqualMatcher());
        $this->control->allow('^192\.168\.0\.1$');

        $this->assertFalse($this->control->evaluate('192.168.0.1'));
        $this->assertTrue($this->control->evaluate('^192\.168\.0\.1$'));
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

// {{{ Stagehand_AccessControlTest

/**
 * @package    Stagehand_AccessControl
 * @copyright  2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 0.1.0
 * @since      Class available since Release 0.1.0
 */
abstract class Stagehand_AccessControlTest extends PHPUnit_Framework_TestCase
{

    // {{{ properties

    /**#@+
     * @access public
     */

    /**#@-*/

    /**#@+
     * @access protected
     */

    protected $control;

    /**#@-*/

    /**#@+
     * @access private
     */

    /**#@-*/

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
