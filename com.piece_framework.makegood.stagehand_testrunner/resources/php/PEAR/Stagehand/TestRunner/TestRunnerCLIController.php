<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2007-2011 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2007-2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.17.0
 * @since      File available since Release 0.5.0
 */

/**
 * A testrunner script to run tests automatically.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2007-2011 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.17.0
 * @since      Class available since Release 0.5.0
 */
class Stagehand_TestRunner_TestRunnerCLIController extends Stagehand_CLIController
{
    protected $exceptionClass = 'Stagehand_TestRunner_Exception';
    protected $shortOptions = 'hVRcp:aw:gm:v';
    protected $longOptions =
        array(
            'growl-password=',
            'log-junit=',
            'log-junit-realtime',
            'classes=',
            'stop-on-failure',
            'phpunit-config=',
            'cakephp-app-path=',
            'cakephp-core-path=',
            'test-file-pattern=',
            'test-file-suffix=',
            'ciunit-path=',
        );
    protected $config;

    /**
     * @param string $framework
     */
    public function __construct($framework)
    {
        $this->config = new Stagehand_TestRunner_Config();
        $this->config->framework = $framework;
        $this->configurePHPRuntimeConfiguration();
    }

    /**
     * @param string $option
     * @param string $value
     * @return boolean
     */
    protected function configureByOption($option, $value)
    {
        switch ($option) {
        case 'h':
            $this->printUsage();
            return false;
        case 'V':
            $this->printVersion();
            return false;
        case 'R':
            $this->config->recursivelyScans = true;
            return true;
        case 'c':
            if (@include_once 'Console/Color.php') {
                $this->config->colors = true;
            }
            return true;
        case 'p':
            $this->config->preloadFile = $value;
            return true;
        case 'a':
            $this->config->enablesAutotest = true;
            return true;
        case 'w':
            $this->config->monitoringDirectories = explode(',', $value);
            return true;
        case 'g':
            if (@include_once 'Net/Growl.php') {
                $this->config->usesGrowl = true;
            }
            return true;
        case '--growl-password':
            $this->config->growlPassword = $value;
            return true;
        case 'm':
            foreach (explode(',', $value) as $testingMethod) {
                $this->config->addTestingMethod($testingMethod);
            }
            return true;
        case '--classes':
            foreach (explode(',', $value) as $testingClass) {
                $this->config->addTestingClass($testingClass);
            }
            return true;
        case '--log-junit':
            $this->config->logsResultsInJUnitXML = true;
            $this->config->junitXMLFile = $value;
            return true;
        case '--log-junit-realtime':
            $this->config->logsResultsInJUnitXMLInRealtime = true;
            return true;
        case '--stop-on-failure':
            $this->config->stopsOnFailure = true;
            return true;
        case '--phpunit-config':
            $this->config->phpunitConfigFile = $value;
            return true;
        case '--cakephp-app-path':
            $this->validateDirectory($value, $option);
            $this->config->cakephpAppPath = $value;
            return true;
        case '--cakephp-core-path':
            $this->validateDirectory($value, $option);
            $this->config->cakephpCorePath = $value;
            return true;
        case '--ciunit-path':
            $this->validateDirectory($value, $option);
            $this->config->ciunitPath = $value;
            return true;
        case '--test-file-pattern':
            $this->config->testFilePattern = $value;
            return true;
        case '--test-file-suffix':
            $this->config->testFileSuffix = $value;
            return true;
        case 'v':
            $this->config->printsDetailedProgressReport = true;
            return true;
        }
    }

    /**
     * @param string $arg
     * @return boolean
     */
    protected function configureByArg($arg)
    {
        $this->config->testingResources[] = $arg;
        return true;
    }

    /**
     */
    protected function doRun()
    {
        if (!count($this->config->testingResources)) {
            $this->config->testingResources[] = $this->config->workingDirectoryAtStartup;
        }

        if (!$this->config->enablesAutotest) {
            $this->runTests();
        } else {
            $this->monitorAlteration();
        }
    }

    /**
     * Prints the usage.
     */
    protected function printUsage()
    {
        echo "USAGE
  {$_SERVER['SCRIPT_NAME']} [OPTIONS] DIRECTORY_OR_FILE1 DIRECTORY_OR_FILE2 ...

NOTES
  If no directories and files are given, {$_SERVER['SCRIPT_NAME']} runs all the tests
  in the current directory.

OPTIONS

  -h
     Prints this help and exit.

  -V
     Prints version information and exit.

  -R
     Recursively runs tests in the specified directory.

  -c
     Colors test results.

  -p FILE
     Preloads FILE before running tests.

  -a
     Monitors for changes in the specified directories and run tests when changes
     are detected.

  -w DIRECTORY1,DIRECTORY2,...
     Specifies one or more directories to be monitored for changes.

  -g
     Notifies test results to Growl.

  --growl-password=PASSWORD
     Specifies PASSWORD for Growl.

  -m METHOD1,METHOD2,...
     Runs only the specified tests in the specified file.
     (PHPUnit, CIUnit, SimpleTest, CakePHP)

  --classes=CLASS1,CLASS2,...
     Runs only the specified test classes in the specified file.
     (PHPUnit, CIUnit, SimpleTest, CakePHP)

  --log-junit=FILE
     Logs test results into the specified file in the JUnit XML format.
     (PHPUnit, CIUnit, SimpleTest, CakePHP, and PHPT)

  --log-junit-realtime
     Logs test results in real-time into the specified file in the JUnit XML format.
     (PHPUnit, CIUnit, SimpleTest, CakePHP, and PHPT)

  -v
     Prints detailed progress report.
     (PHPUnit, CIUnit, and PHPT)

  --stop-on-failure
     Stops the test run when the first failure or error is raised.
     (PHPUnit, CIUnit, SimpleTest, CakePHP, and PHPT)

  --phpunit-config=FILE
     Configures the PHPUnit runtime environment by the specified XML configuration
     file.
     (PHPUnit and CIUnit)

  --cakephp-app-path=DIRECTORY
     Specifies the path of your app folder.
     By default, the current working directory is used.
     (CakePHP)

  --cakephp-core-path=DIRECTORY
     Specifies the path of your CakePHP libraries folder (/path/to/cake).
     By default, the \"cake\" directory under the parent directory of your app
     folder is used. (/path/to/app/../cake)
     (CakePHP)

  --ciunit-path=DIRECTORY
     Specifies the path of your CIUnit tests directory.
     By default, the current working directory is used.
     (CIUnit)

  --test-file-pattern=PATTERN
     Specifies the pattern of your test files by a regular expression literal.
     The default values are:
       PHPUnit: Test(?:Case)?\.php$
       CIUnit:  ^test.+\.php$
       SimpleTest: Test(?:Case)?\.php$
       CakePHP: \.test\.php$
       PHPT: -
       PHPSpec: Spec\.php$
     (PHPUnit, CIUnit, SimpleTest, CakePHP, and PHPSpec)

  --test-file-suffix=SUFFIX
     (deprecated in Stagehand_TestRunner 2.16.0)
     Specifies the suffix of your test files by a regular expression literal.
     The regular expression literal must not contain *.php*.
     (PHPUnit, CIUnit, SimpleTest, CakePHP, and PHPSpec)
";
    }

    /**
     * Prints the version.
     */
    protected function printVersion()
    {
        echo "Stagehand_TestRunner 2.17.0 ({$this->config->framework})

Copyright (c) 2005-2011 KUBO Atsuhiro <kubo@iteman.jp>,
              2007 Masahiko Sakamoto <msakamoto-sf@users.sourceforge.net>,
              2010 KUMAKURA Yousuke <kumatch@gmail.com>,
All rights reserved.
";
    }

    /**
     * Monitors for changes in one or more target directories and runs tests in
     * the test directory recursively when changes are detected. And also the test
     * directory is always added to the directories to be monitored.
     *
     * @throws Stagehand_TestRunner_Exception
     * @since Method available since Release 2.1.0
     */
    protected function monitorAlteration()
    {
        $monitoringDirectories = array();
        foreach (array_merge($this->config->monitoringDirectories,
                             $this->config->testingResources) as $directory
                 ) {
            if (!is_dir($directory)) {
                throw new Stagehand_TestRunner_Exception(
                    'A specified path [ ' .
                    $directory .
                    ' ] is not found or not a directory'
                                                         );
            }

            $directory = realpath($directory);
            if ($directory === false) {
                throw new Stagehand_TestRunner_Exception(
                    'Cannnot get the absolute path of a specified directory [ ' .
                    $directory .
                    ' ]. Make sure all elements of the absolute path have valid permissions.'
                                                         );
            }

            if (!in_array($directory, $monitoringDirectories)) {
                $monitoringDirectories[] = $directory;
            }
        }

        if (array_key_exists('_', $_SERVER)) {
            $command = $_SERVER['_'];
        } elseif (array_key_exists('PHP_COMMAND', $_SERVER)) {
            $command = $_SERVER['PHP_COMMAND'];
        } else {
            $command = $_SERVER['argv'][0];
        }

        $options = array();
        if (preg_match('!^/cygdrive/([a-z])/(.+)!', $command, $matches)) {
            $command = $matches[1] . ':\\' . str_replace('/', '\\', $matches[2]);
        }

        if (!preg_match('/(?:phpspec|phpt|phpunit|simpletest)runner$/', $command)) {
            $configFile = get_cfg_var('cfg_file_path');
            if ($configFile !== false) {
                $options[] = '-c';
                $options[] = dirname($configFile);
            }

            $options[] = $_SERVER['argv'][0];
        }

        $options[] = '-R';

        if (!is_null($this->config->preloadFile)) {
            $options[] = '-p ' . $this->config->preloadFile;
        }

        if ($this->config->colors) {
            $options[] = '-c';
        }

        if ($this->config->usesGrowl) {
            $options[] = '-g';
        }

        if (!is_null($this->config->growlPassword)) {
            $options[] = '--growl-password=' . $this->config->growlPassword;
        }

        foreach ($this->config->testingResources as $testingResource) {
            $options[] = $testingResource;
        }

        $this->createAlterationMonitor($monitoringDirectories, $command, $options)->monitor();
    }

    /**
     * Runs tests.
     *
     * @since Method available since Release 2.1.0
     */
    protected function runTests()
    {
        $runner = new Stagehand_TestRunner_TestRunner($this->config);
        $runner->run();
    }

    /**
     * @param array  $monitoringDirectories
     * @param string $command
     * @param array  $options
     * @return Stagehand_AlterationMonitor
     * @since Method available since Release 2.13.0
     */
    protected function createAlterationMonitor(array $monitoringDirectories, $command, array $options)
    {
        return new Stagehand_AlterationMonitor(
                       $monitoringDirectories,
                       create_function(
                           '',
                           "passthru('" .
                           $command .
                           ' ' .
                           implode(' ', $options) .
                           "');"
                       )
               );
    }

    /**
     * @since Method available since Release 2.14.0
     */
    protected function configurePHPRuntimeConfiguration()
    {
        ini_set('display_errors', true);
        ini_set('html_errors', false);
        ini_set('implicit_flush', true);
        ini_set('max_execution_time', 0);
    }

    /**
     * @param string $directory
     * @param string $option
     * @throws Stagehand_TestRunner_Exception
     * @since Method available since Release 2.14.0
     */
    protected function validateDirectory($directory, $option)
    {
        if (!is_readable($directory)) {
            throw new Stagehand_TestRunner_Exception(
                      'The specified path [ ' .
                      $directory .
                      ' ] by the ' .
                      $option .
                      ' option is not found or not readable.'
                  );
        }

        if (!is_dir($directory)) {
            throw new Stagehand_TestRunner_Exception(
                      'The specified path [ ' .
                      $directory .
                      ' ] by the ' .
                      $option .
                      ' option is not a directory.'
                  );
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
