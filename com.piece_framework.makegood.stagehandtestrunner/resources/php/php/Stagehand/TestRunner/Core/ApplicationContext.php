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
 * @version    Release: 3.3.1
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\Core;

use Stagehand\ComponentFactory\ComponentFactory;

use Stagehand\TestRunner\Core\Plugin\IPlugin;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.3.1
 * @since      Class available since Release 3.0.0
 */
class ApplicationContext
{
    /**
     * @var \Stagehand\TestRunner\Core\ApplicationContext
     */
    private static $soleInstance;

    /**
     * @var \Stagehand\ComponentFactory\ComponentFactory
     */
    protected $componentFactory;

    /**
     * @var \Stagehand\TestRunner\Core\Environment
     */
    protected $environment;

    /**
     * @var \Stagehand\TestRunner\Core\Plugin\IPlugin
     */
    protected $plugin;

    /**
     * @return \Stagehand\TestRunner\Core\ApplicationContext
     */
    public static function getInstance()
    {
        return self::$soleInstance;
    }

    /**
     * @param \Stagehand\TestRunner\Core\ApplicationContext $applicationContext
     */
    public static function setInstance(ApplicationContext $applicationContext)
    {
        self::$soleInstance = $applicationContext;
    }

    /**
     * @param \Stagehand\ComponentFactory\ComponentFactory $componentFactory
     */
    public function setComponentFactory(ComponentFactory $componentFactory)
    {
        $this->componentFactory = $componentFactory;
    }

    /**
     * @return \Stagehand\ComponentFactory\ComponentFactory
     */
    public function getComponentFactory()
    {
        return $this->componentFactory;
    }

    /**
     * @param string $componentID
     * @return mixed
     */
    public function createComponent($componentID)
    {
        return $this->componentFactory->create($componentID);
    }

    /**
     * @param string $componentID
     * @param mixed $component
     */
    public function setComponent($componentID, $component)
    {
        $this->componentFactory->set($componentID, $component);
    }

    /**
     * @param \Stagehand\TestRunner\Core\Environment $environment
     */
    public function setEnvironment(Environment $environment)
    {
        $this->environment = $environment;
    }

    /**
     * @return \Stagehand\TestRunner\Core\Environment
     */
    public function getEnvironment()
    {
        return $this->environment;
    }

    /**
     * @param \Stagehand\TestRunner\Core\Plugin\IPlugin $plugin
     */
    public function setPlugin(IPlugin $plugin)
    {
        $this->plugin = $plugin;
    }

    /**
     * @return \Stagehand\TestRunner\Core\Plugin\IPlugin
     */
    public function getPlugin()
    {
        return $this->plugin;
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
