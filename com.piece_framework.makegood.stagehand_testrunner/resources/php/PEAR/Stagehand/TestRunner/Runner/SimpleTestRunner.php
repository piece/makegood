<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2007 Masahiko Sakamoto <msakamoto-sf@users.sourceforge.net>,
 *               2007-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 *               2010 KUMAKURA Yousuke <kumatch@gmail.com>,
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
 * @copyright  2007-2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @copyright  2010 KUMAKURA Yousuke <kumatch@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.20.0
 * @link       http://simpletest.org/
 * @since      File available since Release 2.1.0
 */

/**
 * A test runner for SimpleTest.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2007 Masahiko Sakamoto <msakamoto-sf@users.sourceforge.net>
 * @copyright  2007-2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @copyright  2010 KUMAKURA Yousuke <kumatch@gmail.com>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.20.0
 * @link       http://simpletest.org/
 * @since      Class available since Release 2.1.0
 */
class Stagehand_TestRunner_Runner_SimpleTestRunner extends Stagehand_TestRunner_Runner
{
    protected $junitXMLFileHandle;
    protected $junitXMLReporterClass = 'Stagehand_TestRunner_Runner_SimpleTestRunner_JUnitXMLReporter';

    /**
     * Runs tests based on the given TestSuite object.
     *
     * @param TestSuite $suite
     */
    public function run($suite)
    {
        $textReporter = new TextReporter();
        $reporter = new MultipleReporter();
        $reporter->attachReporter($this->decorateReporter($textReporter));

        if ($this->config->logsResultsInJUnitXML()) {
            if (!$this->config->logsResultsInJUnitXMLInRealtime()) {
                $xmlWriter =
                    new Stagehand_TestRunner_JUnitXMLWriter_JUnitXMLDOMWriter(
                        array($this, 'writeJUnitXMLToFile')
                    );
            } else {
                $xmlWriter = $this->junitXMLStreamWriter(
                                 array($this, 'writeJUnitXMLToFile')
                             );
            }

            $junitXMLReporter = new $this->junitXMLReporterClass($this->config);
            $junitXMLReporter->setXMLWriter($xmlWriter);
            $junitXMLReporter->setTestSuite($suite);
            $junitXMLReporter->setConfig($this->config);
            $reporter->attachReporter($this->decorateReporter($junitXMLReporter));
        }

        ob_start();
        $suite->run($reporter);
        $output = ob_get_contents();
        ob_end_clean();

        if ($this->config->logsResultsInJUnitXML()) {
            if (is_resource($this->junitXMLFileHandle)) {
                fclose($this->junitXMLFileHandle);
            }
        }

        if ($this->config->usesNotification) {
            if ($textReporter->getFailCount() + $textReporter->getExceptionCount() == 0) {
                $notificationResult = Stagehand_TestRunner_Notification_Notification::RESULT_PASSED;
            } else {
                $notificationResult = Stagehand_TestRunner_Notification_Notification::RESULT_FAILED;
            }

            preg_match('/^((?:OK|FAILURES).+)/ms', $output, $matches);
            $this->notification = new Stagehand_TestRunner_Notification_Notification($notificationResult, $matches[1]);
        }

        if ($this->config->colors()) {
            print Console_Color::convert(preg_replace(array('/^(OK.+)/ms',
                                                            '/^(FAILURES!!!.+)/ms',
                                                            '/^(\d+\)\s)(.+at \[.+\]$\s+in .+)$/m',
                                                            '/^(Exception \d+!)/m',
                                                            '/^(Unexpected exception of type \[.+\] with message \[.+\] in \[.+\]$\s+in .+)$/m'),
                                                      array('%g$1%n',
                                                            '%r$1%n',
                                                            "\$1%r\$2%n",
                                                            '%p$1%n',
                                                            '%p$1%n'),
                                                      Console_Color::escape($output))
                                         );
        } else {
            print $output;
        }
    }

    /**
     * @param string $buffer
     * @throws Stagehand_TestRunner_Exception
     * @since Method available since Release 2.10.0
     */
    public function writeJUnitXMLToFile($buffer)
    {
        if (!is_resource($this->junitXMLFileHandle)) {
            $result = fopen($this->config->getJUnitXMLFile(), 'w');
            if (!$result) {
                throw new Stagehand_TestRunner_Exception(
                   'Failed to open the specified file [ ' .
                   $this->config->getJUnitXMLFile() .
                   ' ].'
                );
            }

            $this->junitXMLFileHandle = $result;
        }

        $result = fwrite($this->junitXMLFileHandle, $buffer, strlen($buffer));
        if ($result === false) {
            throw new Stagehand_TestRunner_Exception(
               'Failed to write the test results into the specified file [ ' .
               $this->config->getJUnitXMLFile() .
               ' ].'
            );
        }
    }

    /**
     * @param callback $streamWriter
     * @return Stagehand_TestRunner_JUnitXMLWriter_JUnitXMLStreamWriter
     * @since Method available since Release 2.10.0
     */
    protected function junitXMLStreamWriter($streamWriter)
    {
        return new Stagehand_TestRunner_JUnitXMLWriter_JUnitXMLStreamWriter($streamWriter);
    }

    /**
     * @param mixed $reporter
     * @return mixed
     * @since Method available since Release 2.10.0
     */
    protected function decorateReporter($reporter)
    {
        $reporters[] = $reporter;

        if ($this->config->testsOnlySpecifiedMethods) {
            $reporters[] = $this->createMethodFilterReporter($reporters[ count($reporters) - 1 ]);
            $reporters[ count($reporters) - 1 ]->setConfig($this->config);
        }

        if ($this->config->testsOnlySpecifiedClasses) {
            $reporters[] = new Stagehand_TestRunner_Runner_SimpleTestRunner_ClassFilterReporter($reporters[ count($reporters) - 1 ]);
            $reporters[ count($reporters) - 1 ]->setConfig($this->config);
        }

        if ($this->config->stopsOnFailure) {
            $reporters[] = new Stagehand_TestRunner_Runner_SimpleTestRunner_StopOnFailureReporter($reporters[ count($reporters) - 1 ]);
        }

        return $reporters[ count($reporters) - 1 ];
    }

    /**
     * @param mixed $reporter
     * @return SimpleReporterDecorator
     * @since Method available since Release 2.14.2
     */
    protected function createMethodFilterReporter($reporter)
    {
        return new Stagehand_TestRunner_Runner_SimpleTestRunner_MethodFilterReporter($reporter);
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
