<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5.3
 *
 * Copyright (c) 2009-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2009-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.2.0
 * @since      File available since Release 2.10.0
 */

namespace Stagehand\TestRunner\JUnitXMLWriter;

use Stagehand\TestRunner\Util\StreamWriter;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2009-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.2.0
 * @since      Class available since Release 2.10.0
 */
class JUnitXMLStreamWriter implements JUnitXMLWriter
{
    /**
     * @var \Stagehand\TestRunner\JUnitXMLWriter\XMLStreamWriter
     */
    protected $xmlWriter;

    /**
     * @param \Stagehand\TestRunner\Util\StreamWriter
     */
    protected $streamWriter;

    /**
     * @var \Stagehand\TestRunner\JUnitXMLWriter\UTF8Converter
     */
    protected $utf8Converter;

    /**
     */
    public function __construct()
    {
        $this->xmlWriter = new XMLStreamWriter();
        $this->utf8Converter = UTF8ConverterFactory::create();
    }

    /**
     * @param \Stagehand\TestRunner\Util\StreamWriter $streamWriter
     * @since Method available since Release 3.0.0
     */
    public function setStreamWriter(StreamWriter $streamWriter)
    {
        $this->streamWriter = $streamWriter;
    }

    public function startTestSuites()
    {
        $this->xmlWriter->startElement('testsuites');
        $this->xmlWriter->closeStartTag();
        $this->flush();
    }

    /**
     * @param string  $name
     * @param integer $testCount
     */
    public function startTestSuite($name, $testCount = null)
    {
        if (preg_match('/^(.+)::(.+)/', $name, $matches)) {
            $name = $matches[2];
            $className = $matches[1];
        } else {
            $className = $name;
        }

        $this->xmlWriter->startElement('testsuite');
        $this->xmlWriter->writeAttribute('name', $this->utf8Converter->convert($name));
        if (!is_null($testCount)) {
            $this->xmlWriter->writeAttribute('tests', $testCount);
        }

        if (strlen($className) > 0 && class_exists($className, false)) {
            try {
                $class = new \ReflectionClass($className);
                $this->xmlWriter->writeAttribute('class', $this->utf8Converter->convert($class->getName()));
                $this->xmlWriter->writeAttribute('file', $this->utf8Converter->convert($class->getFileName()));
            } catch (\ReflectionException $e) {
            }
        }

        $this->xmlWriter->closeStartTag();
        $this->flush();
    }

    /**
     * @param string $name
     * @param mixed  $test
     * @param string $methodName
     */
    public function startTestCase($name, $test, $methodName = null)
    {
        $this->xmlWriter->startElement('testcase');
        $this->xmlWriter->writeAttribute('name', $this->utf8Converter->convert($name));

        $class = new \ReflectionClass($test);
        if (is_null($methodName)) {
            $methodName = $name;
        }
        if ($class->hasMethod($methodName)) {
            $method = $class->getMethod($methodName);
            $this->xmlWriter->writeAttribute('class', $this->utf8Converter->convert($method->getDeclaringClass()->getName()));
            $this->xmlWriter->writeAttribute('method', $method->getName());
            $this->xmlWriter->writeAttribute('file', $this->utf8Converter->convert($method->getDeclaringClass()->getFileName()));
            $this->xmlWriter->writeAttribute('line', $method->getStartLine());
        }

        $this->xmlWriter->closeStartTag();
        $this->flush();
    }

    /**
     * @param string $text
     * @param string $type
     * @param string $file
     * @param string $line
     * @param string $message
     */
    public function writeError($text, $type = null, $file = null, $line = null, $message = null)
    {
        $this->writeFailureOrError($text, $type, 'error', $file, $line, $message);
    }

    /**
     * @param string $text
     * @param string $type
     * @param string $file
     * @param string $line
     * @param string $message
     */
    public function writeFailure($text, $type = null, $file = null, $line = null, $message = null)
    {
        $this->writeFailureOrError($text, $type, 'failure', $file, $line, $message);
    }

    /**
     * @param float   $time
     * @param integer $assertionCount
     */
    public function endTestCase($time, $assertionCount = null)
    {
        $this->endElementAndFlush();
    }

    public function endTestSuite()
    {
        $this->endElementAndFlush();
    }

    public function endTestSuites()
    {
        $this->xmlWriter->endElement();
        $this->flush();
        $this->streamWriter->close();
    }

    /**
     * @param string $text
     * @param string $type
     * @param string $failureOrError
     * @param string $file
     * @param string $line
     * @param string $message
     */
    protected function writeFailureOrError($text, $type, $failureOrError, $file, $line, $message)
    {
        $this->xmlWriter->startElement($failureOrError);
        if (!is_null($type)) {
            $this->xmlWriter->writeAttribute('type', $this->utf8Converter->convert($type));
        }
        if (!is_null($file)) {
            $this->xmlWriter->writeAttribute('file', $this->utf8Converter->convert($file));
        }
        if (!is_null($line)) {
            $this->xmlWriter->writeAttribute('line', $line);
        }
        if (!is_null($message)) {
            $this->xmlWriter->writeAttribute('message', $this->utf8Converter->convert($message));
        }
        $this->xmlWriter->text($this->utf8Converter->convert($text));
        $this->xmlWriter->endElement();

        $this->flush();
    }

    protected function endElementAndFlush()
    {
        $this->xmlWriter->endElement();
        $this->flush();
    }

    protected function flush()
    {
        $this->streamWriter->write($this->xmlWriter->flush());
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
