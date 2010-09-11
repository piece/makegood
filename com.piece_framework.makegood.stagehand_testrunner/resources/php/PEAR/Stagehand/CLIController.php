<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2008-2009 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @package    Stagehand_CLIController
 * @copyright  2008-2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.0.0
 * @since      File available since Release 0.1.0
 */

require_once 'Console/Getopt.php';

// {{{ Stagehand_CLIController

/**
 * @package    Stagehand_CLIController
 * @copyright  2008-2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.0.0
 * @since      Class available since Release 0.1.0
 */
abstract class Stagehand_CLIController
{

    // {{{ properties

    /**#@+
     * @access public
     */

    /**#@-*/

    /**#@+
     * @access protected
     */

    protected $exceptionClass = 'Exception';
    protected $shortOptions;
    protected $longOptions = array();

    /**#@-*/

    /**#@+
     * @access private
     */

    /**#@-*/

    /**#@+
     * @access public
     */

    // }}}
    // {{{ run()

    /**
     * @return integer
     * @throws Exception
     */
    public function run()
    {
        if (!array_key_exists('argv', $_SERVER)) {
            echo "ERROR: either use the CLI php executable, or set register_argc_argv=On in php.ini\n";;
            return 1;
        }

        try {
            list($options, $args) = $this->parseOptions();
            $continues = $this->configure($options, $args);
            if (!$continues) {
                return 0;
            }

            $this->doRun();
        } catch (Exception $e) {
            if (!$e instanceof $this->exceptionClass) {
                throw $e;
            }

            echo 'ERROR: ' . $e->getMessage() . "\n";
            return 1;
        }

        return 0;
    }

    /**#@-*/

    /**#@+
     * @access protected
     */

    // }}}
    // {{{ configureByOption()

    /**
     * @param string $option
     * @param string $value
     * @return boolean
     */
    abstract protected function configureByOption($option, $value);

    // }}}
    // {{{ configureByArg()

    /**
     * @param string $arg
     * @return boolean
     */
    abstract protected function configureByArg($arg);

    // }}}
    // {{{ doRun()

    /**
     */
    abstract protected function doRun();

    // }}}
    // {{{ parseOptions()

    /**
     * Parses the command line options.
     *
     * @return array
     */
    protected function parseOptions()
    {
        Stagehand_LegacyError_PEARError::enableConversion();
        $oldErrorReportingLevel = error_reporting(error_reporting() & ~E_STRICT & ~E_NOTICE);
        try {
            $argv = Console_Getopt::readPHPArgv();
            array_shift($argv);
            $parsedOptions = Console_Getopt::getopt2($argv, $this->shortOptions, $this->longOptions);
        } catch (Stagehand_LegacyError_PEARError_Exception $e) {
            error_reporting($oldErrorReportingLevel);
            Stagehand_LegacyError_PEARError::disableConversion();
            throw new $this->exceptionClass(preg_replace('/^Console_Getopt: /', '', $e->getMessage()));
        } catch (Exception $e) {
            error_reporting($oldErrorReportingLevel);
            Stagehand_LegacyError_PEARError::disableConversion();
            throw $e;
        }
        error_reporting($oldErrorReportingLevel);
        Stagehand_LegacyError_PEARError::disableConversion();

        return $parsedOptions;
    }

    // }}}
    // {{{ configure()

    /**
     * Configures the current process by the command line options and arguments.
     *
     * @param array $options
     * @param array $args
     * @return boolean
     */
    protected function configure(array $options, array $args)
    {
        foreach ($options as $option) {
            $continues = $this->configureByOption($option[0], @$option[1]);
            if (!$continues) {
                return false;
            }
        }

        foreach ($args as $arg) {
            $continues = $this->configureByArg($arg);
            if (!$continues) {
                return false;
            }
        }

        return true;
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
