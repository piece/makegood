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
 * @version    Release: 3.0.2
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\Core\Configuration;

use Symfony\Component\Config\Definition\Builder\NodeBuilder;

use Stagehand\TestRunner\Core\ApplicationContext;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.0.2
 * @since      Class available since Release 3.0.0
 */
class GeneralConfiguration extends Configuration
{
    /**
     * @var string
     */
    private static $CONFIGURATION_ID = 'general';

    public static function getConfigurationID()
    {
        return self::$CONFIGURATION_ID;
    }

    /**
     * @param \Symfony\Component\Config\Definition\Builder\NodeBuilder $nodeBuilder
     */
    protected function defineGrammar(NodeBuilder $nodeBuilder)
    {
        $nodeBuilder
            ->scalarNode('framework') // This element is just a label for YAML-based configuration.
            ->end()
            ->arrayNode('test_targets')
                ->addDefaultsIfNotSet()
                ->children()
                    ->arrayNode('resources')
                        ->defaultValue(array(ApplicationContext::getInstance()->getEnvironment()->getWorkingDirectoryAtStartup()))
                        ->validate()
                            ->ifTrue(function ($v) { return count($v) == 0; })
                            ->then(function ($v) { return array(ApplicationContext::getInstance()->getEnvironment()->getWorkingDirectoryAtStartup()); })
                        ->end()
                        ->prototype('scalar')
                            ->cannotBeEmpty()
                        ->end()
                    ->end()
                    ->booleanNode('recursive')
                        ->defaultFalse()
                    ->end()
                    ->arrayNode('methods')
                        ->defaultValue(array())
                        ->prototype('scalar')
                            ->cannotBeEmpty()
                        ->end()
                    ->end()
                    ->arrayNode('classes')
                        ->defaultValue(array())
                        ->prototype('scalar')
                            ->cannotBeEmpty()
                        ->end()
                    ->end()
                    ->scalarNode('file_pattern')
                        ->defaultNull()
                        ->cannotBeEmpty()
                    ->end()
                ->end()
            ->end()
            ->arrayNode('autotest')
                ->addDefaultsIfNotSet()
                ->treatNullLike(array('enabled' => true))
                ->treatTrueLike(array('enabled' => true))
                ->treatFalseLike(array('enabled' => false))
                ->children()
                    ->booleanNode('enabled')
                        ->defaultFalse()
                    ->end()
                    ->arrayNode('watch_dirs')
                        ->defaultValue(array())
                        ->prototype('scalar')
                            ->cannotBeEmpty()
                        ->end()
                    ->end()
                ->end()
            ->end()
            ->booleanNode('notify')
                ->defaultFalse()
            ->end()
            ->arrayNode('junit_xml')
                ->addDefaultsIfNotSet()
                ->beforeNormalization()
                    ->ifString()
                    ->then(function ($v) { return array('file' => $v); })
                ->end()
                ->children()
                    ->scalarNode('file')
                        ->defaultNull()
                        ->cannotBeEmpty()
                    ->end()
                    ->booleanNode('realtime')
                        ->defaultFalse()
                    ->end()
                ->end()
            ->end()
            ->booleanNode('stop_on_failure')
                ->defaultFalse()
            ->end()
            ->booleanNode('detailed_progress')
                ->defaultFalse()
            ->end()
        ;
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
