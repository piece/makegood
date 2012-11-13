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
 * @version    Release: 3.4.0
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\DependencyInjection\Extension;

use Stagehand\TestRunner\Core\Plugin\PluginRepository;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.4.0
 * @since      Class available since Release 3.0.0
 */
class ExtensionRepository
{
    /**
     * @return array
     * @throws \Stagehand\TestRunner\DependencyInjection\Extension\FrameworkUnavailableException
     */
    public static function findAll()
    {
        $plugins = PluginRepository::findAll();
        if (count($plugins) == 0) {
            throw new FrameworkUnavailableException('Stagehand_TestRunner is unavailable since no plugins are found in this installation.');
        }

        $extensions = array(new GeneralExtension());
        foreach ($plugins as $plugin) { /* @var $plugin \Stagehand\TestRunner\Core\Plugin\IPlugin */
            $extensionClass = new \ReflectionClass(__NAMESPACE__ . '\\' . $plugin->getPluginID() . 'Extension');
            if (!$extensionClass->isInterface()
                && !$extensionClass->isAbstract()
                && $extensionClass->isSubclassOf('Symfony\Component\DependencyInjection\Extension\ExtensionInterface')) {
                $extensions[] = $extensionClass->newInstance();
            }
        }

        return $extensions;
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
