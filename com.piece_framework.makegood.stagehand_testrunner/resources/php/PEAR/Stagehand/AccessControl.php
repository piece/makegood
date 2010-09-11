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
 * @version    Release: 1.0.0
 * @since      File available since Release 0.1.0
 */

// {{{ Stagehand_AccessControl

/**
 * @package    Stagehand_AccessControl
 * @copyright  2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.0.0
 * @since      Class available since Release 0.1.0
 */
class Stagehand_AccessControl
{

    // {{{ properties

    /**#@+
     * @access public
     */

    /**#@-*/

    /**#@+
     * @access protected
     */

    protected $denyRules = array();
    protected $allowRules = array();
    protected $order;
    protected $matcher;

    /**#@-*/

    /**#@+
     * @access private
     */

    /**#@-*/

    /**#@+
     * @access public
     */

    // }}}
    // {{{ __construct()

    /**
     * @param Stagehand_AccessControl_Order $order
     */
    public function __construct(Stagehand_AccessControl_Order $order)
    {
        $this->order = $order;
        $this->setMatcher(new Stagehand_AccessControl_Matcher_RegexMatcher());
    }

    // }}}
    // {{{ denyAllow()

    /**
     * @return Stagehand_AccessControl
     */
    public static function denyAllow()
    {
        return new self(new Stagehand_AccessControl_Order_DenyAllowOrder());
    }

    // }}}
    // {{{ allowDeny()

    /**
     * @return Stagehand_AccessControl
     */
    public static function allowDeny()
    {
        return new self(new Stagehand_AccessControl_Order_AllowDenyOrder());
    }

    // }}}
    // {{{ evaluate()

    /**
     * @param string $target
     * @return boolean
     */
    public function evaluate($target)
    {
        if ($this->order->firstPassProcess() == Stagehand_AccessControl_AccessState::DENY) {
            if ($this->matchDenyRules($target) == Stagehand_AccessControl_AccessState::DENY) {
                return $this->matchAllowRules($target);
            }
        } elseif ($this->order->firstPassProcess() == Stagehand_AccessControl_AccessState::ALLOW) {
            if ($this->matchAllowRules($target) == Stagehand_AccessControl_AccessState::ALLOW) {
                return $this->matchDenyRules($target);
            }
        }

        return $this->order->defaultAccessState();
    }

    // }}}
    // {{{ deny()

    /**
     * @param string $rule
     */
    public function deny($rule)
    {
        $this->denyRules[] = $rule;
    }

    // }}}
    // {{{ allow()

    /**
     * @param string $rule
     */
    public function allow($rule)
    {
        $this->allowRules[] = $rule;
    }

    // }}}
    // {{{ setMatcher()

    /**
     * @param Stagehand_AccessControl_Matcher $matcher
     */
    public function setMatcher(Stagehand_AccessControl_Matcher $matcher)
    {
        $this->matcher = $matcher;
    }

    /**#@-*/

    /**#@+
     * @access protected
     */

    // }}}
    // {{{ matchDenyRules()

    /**
     * @param string $target
     * @return boolean
     */
    protected function matchDenyRules($target)
    {
        foreach ($this->denyRules as $denyRule) {
            if ($this->matcher->match($target, $denyRule)) {
                return Stagehand_AccessControl_AccessState::DENY;
            }
        }

        return Stagehand_AccessControl_AccessState::ALLOW;
    }

    // }}}
    // {{{ matchAllowRules()

    /**
     * @param string $target
     * @return boolean
     */
    protected function matchAllowRules($target)
    {
        foreach ($this->allowRules as $allowRule) {
            if ($this->matcher->match($target, $allowRule)) {
                return Stagehand_AccessControl_AccessState::ALLOW;
            }
        }

        return Stagehand_AccessControl_AccessState::DENY;
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
