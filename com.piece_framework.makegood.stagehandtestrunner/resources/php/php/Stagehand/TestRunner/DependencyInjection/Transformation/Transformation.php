<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2011-2013 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2011-2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\DependencyInjection\Transformation;

use Symfony\Component\DependencyInjection\ContainerInterface;
use Symfony\Component\Yaml\Yaml;

use Stagehand\TestRunner\Core\Plugin\PluginInterface;
use Stagehand\TestRunner\Core\Plugin\PluginRepository;
use Stagehand\TestRunner\DependencyInjection\Configuration\GeneralConfiguration;
use Stagehand\TestRunner\Util\String;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2013 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.6.1
 * @since      Class available since Release 3.0.0
 */
class Transformation
{
    /**
     * @var array
     */
    protected $configuration = array();

    /**
     * @var \Symfony\Component\DependencyInjection\ContainerInterface
     */
    protected $container;

    /**
     * @var \Stagehand\TestRunner\Core\Plugin\PluginInterface
     * @since Property available since Release 3.6.0
     */
    protected $plugin;

    /**
     * @var string
     */
    protected $configurationFile;

    /**
     * @param \Symfony\Component\DependencyInjection\ContainerInterface $container
     * @param \Stagehand\TestRunner\Core\Plugin\PluginInterface $plugin
     */
    public function __construct(ContainerInterface $container, PluginInterface $plugin)
    {
        $pluginConfigurationClass =
            'Stagehand\TestRunner\DependencyInjection\Configuration' . '\\' .
            $plugin->getPluginID() . 'Configuration';
        $this->configuration[ $pluginConfigurationClass::getConfigurationID() ] = array();
        $this->configuration[ GeneralConfiguration::getConfigurationID() ] = array();

        $this->container = $container;
        $this->plugin = $plugin;
    }

    /**
     * @param string $configurationID
     * @param array $configurationPart
     * @throws \InvalidArgumentException
     */
    public function setConfigurationPart($configurationID, array $configurationPart)
    {
        if (!array_key_exists($configurationID, $this->configuration)) {
            throw new \InvalidArgumentException(sprintf(
                'The configuration ID must be a one of %s, [ %s ] is given.',
                implode(' and ', array_keys($this->configuration)),
                $configurationID
            ));
        }

        $this->configuration[$configurationID][] = $configurationPart;
    }

    public function transformToContainerParameters()
    {
        if (!is_null($this->configurationFile)) {
            foreach (Yaml::parse($this->configurationFile) as $configurationID => $configurationPart) {
                if (!is_null($configurationPart)) {
                    if (!array_key_exists($configurationID, $this->configuration)) {
                        $this->configuration[$configurationID] = array();
                    }

                    array_unshift($this->configuration[$configurationID], $configurationPart);
                }
            }
        }

        foreach ($this->configuration as $configurationID => $configurationParts) {
            $configurations = array();
            foreach ($configurationParts as $configurationPart) {
                $configurations[] = $configurationPart;
            }

            if ($configurationID == GeneralConfiguration::getConfigurationID()) {
                $transformerID = 'General';
            } else {
                $plugin = PluginRepository::findByPluginID($configurationID);
                $transformerID = $plugin->getPluginID();
            }
            $transformerClass = __NAMESPACE__ . '\\' . $transformerID . 'Transformer';
            $transformer = new $transformerClass($configurations, $this->container); /* @var $transformer \Stagehand\TestRunner\DependencyInjection\Transformation\Transformer */
            $transformer->transform();
        }

        if (is_null($this->container->getParameter('test_file_pattern'))) {
            $this->container->setParameter(
                'test_file_pattern',
                $this->plugin->getTestFilePattern()
            );
        }
    }

    /**
     * @param string $configurationFile
     */
    public function setConfigurationFile($configurationFile)
    {
        $this->configurationFile = $configurationFile;
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
