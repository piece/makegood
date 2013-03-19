<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2012-2013 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2012-2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.0
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\DependencyInjection\Compiler;

use Stagehand\ComponentFactory\UnfreezableContainerBuilder;
use Symfony\Component\DependencyInjection\Compiler\CompilerPassInterface;
use Symfony\Component\DependencyInjection\Compiler\ResolveParameterPlaceHoldersPass;

use Stagehand\TestRunner\Core\Plugin\PluginRepository;
use Stagehand\TestRunner\DependencyInjection\Compiler\ReplaceDefinitionByPluginDefinitionPass;
use Stagehand\TestRunner\DependencyInjection\Extension\GeneralExtension;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2012-2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.0
 * @since      Class available since Release 3.0.0
 */
class Compiler
{
    const COMPILED_CONTAINER_NAMESPACE = 'Stagehand\TestRunner\DependencyInjection';

    public function compile()
    {
        foreach (PluginRepository::findAll() as $plugin) {
            $containerBuilder = new UnfreezableContainerBuilder();
            $containerBuilder->registerExtension(new GeneralExtension());

            $extensionClass = new \ReflectionClass('Stagehand\TestRunner\DependencyInjection\Extension\\' . $plugin->getPluginID() . 'Extension');
            if (!$extensionClass->isInterface()
                && !$extensionClass->isAbstract()
                && $extensionClass->isSubclassOf('Symfony\Component\DependencyInjection\Extension\ExtensionInterface')) {
                $containerBuilder->registerExtension($extensionClass->newInstance());
            }

            foreach ($containerBuilder->getExtensions() as $extension) { /* @var $extension \Symfony\Component\DependencyInjection\Extension\ExtensionInterface */
                $containerBuilder->loadFromExtension($extension->getAlias(), array());
            }

            $containerBuilder->addCompilerPass(new ReplaceDefinitionByPluginDefinitionPass($plugin));
            $containerBuilder->getCompilerPassConfig()->setOptimizationPasses(
                array_filter(
                    $containerBuilder->getCompilerPassConfig()->getOptimizationPasses(),
                    function (CompilerPassInterface $compilerPass) {
                        return !($compilerPass instanceof ResolveParameterPlaceHoldersPass);
                    }
            ));

            $containerClass = $plugin->getPluginID() . 'Container';
            $compiler = new \Stagehand\ComponentFactory\Compiler($containerBuilder, $containerClass, self::COMPILED_CONTAINER_NAMESPACE);
            $containerSource = $compiler->compile();
            file_put_contents(__DIR__ . '/../' . $containerClass . '.php', $containerSource);
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
