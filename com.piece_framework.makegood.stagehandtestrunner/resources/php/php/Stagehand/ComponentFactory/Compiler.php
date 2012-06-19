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
 * @package    Stagehand_ComponentFactory
 * @copyright  2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.0.1
 * @since      File available since Release 1.0.0
 */

namespace Stagehand\ComponentFactory;

use Symfony\Component\DependencyInjection\ContainerBuilder;
use Symfony\Component\DependencyInjection\Dumper\PhpDumper;

/**
 * A Compiler that compiles a ContainerBuilder object into a PHP class with
 * the specified class and namespace.
 *
 * @package    Stagehand_ComponentFactory
 * @copyright  2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.0.1
 * @since      Class available since Release 1.0.0
 */
class Compiler
{
    /**
     * @var \Symfony\Component\DependencyInjection\ContainerBuilder
     */
    protected $containerBuilder;

    /**
     * @var string
     */
    protected $class;

    /**
     * @var string
     */
    protected $namespace;

    /**
     * @param \Symfony\Component\DependencyInjection\ContainerBuilder $containerBuilder
     * @param string $class
     * @param string $namespace
     */
    public function __construct(ContainerBuilder $containerBuilder, $class, $namespace = null)
    {
        $this->containerBuilder = $containerBuilder;
        $this->class = $class;
        $this->namespace = $namespace;
    }

    /**
     * @param boolean $evaluatable
     * @return string
     */
    public function compile($evaluatable = false)
    {
        $this->containerBuilder->compile();

        $phpDumper = new PhpDumper($this->containerBuilder);
        $containerClassSource = $phpDumper->dump(array('class' => $this->class));

        if (!is_null($this->namespace)) {
            $containerClassSource = preg_replace(
                '/^<\?php/',
                '<?php' . PHP_EOL . 'namespace ' . $this->namespace . ';' . PHP_EOL,
                $containerClassSource
            );
        }

        if ($evaluatable) {
            $containerClassSource = preg_replace('/^<\?php/', '', $containerClassSource);
        }

        return $containerClassSource;
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
