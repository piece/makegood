<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2009 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @package    Stagehand_DirectoryScanner
 * @copyright  2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.1.0
 * @since      File available since Release 1.0.0
 */

// {{{ Stagehand_DirectoryScanner

/**
 * A directory scanner.
 *
 * @package    Stagehand_DirectoryScanner
 * @copyright  2009 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 1.1.0
 * @since      Class available since Release 1.0.0
 */
class Stagehand_DirectoryScanner
{

    // {{{ properties

    /**#@+
     * @access public
     */

    /**#@-*/

    /**#@+
     * @access protected
     */

    protected $recursivelyScans = true;
    protected $callback;
    protected $denyAllow;

    /**
     * The CVS excludes defined in rsync 3.0.5
     */
    protected $cvsExcludes = array('^RCS$',
                                   '^SCCS$',
                                   '^CVS$',
                                   '^CVS\.adm$',
                                   '^RCSLOG$',
                                   '^cvslog\.',
                                   '^tags$',
                                   '^TAGS$',
                                   '^\.make\.state$',
                                   '^\.nse_depinfo$',
                                   '~$',
                                   '^#',
                                   '^\.#',
                                   '^,',
                                   '^_\$',
                                   '\$$',
                                   '\.old$',
                                   '\.bak$',
                                   '\.BAK$',
                                   '\.orig$',
                                   '\.rej$',
                                   '^\.del-',
                                   '\.a$',
                                   '\.olb$',
                                   '\.o$',
                                   '\.obj$',
                                   '\.so$',
                                   '\.exe$',
                                   '\.Z$',
                                   '\.elc$',
                                   '\.ln$',
                                   '^core$',
                                   '^\.svn$',
                                   '^\.git$',
                                   '^\.bzr$'
                                   );

    /**#@-*/

    /**#@+
     * @access private
     */

    /**#@-*/

    /**#@+
     * @access public
     */

    // }}}
    // {{{ __construct()

    /**
     * Sets the callback to the properties.
     *
     * @param callback $callback
     * @param boolean  $useCVSExcludes
     */
    public function __construct($callback, $useCVSExcludes = true)
    {
        $this->callback = $callback;
        $this->denyAllow = Stagehand_AccessControl::denyAllow();
        $this->addExclude('^\.$');
        $this->addExclude('^\.\.$');
        if ($useCVSExcludes) {
            foreach ($this->cvsExcludes as $cvsExclude) {
                $this->addExclude($cvsExclude);
            }
        }
    }

    // }}}
    // {{{ scan()

    /**
     * Scans the directory and invoke the callback.
     *
     * @param string $directory
     * @throws Stagehand_DirectoryScanner_Exception
     */
    public function scan($directory)
    {
        Stagehand_LegacyError_PHPError::enableConversion(E_WARNING);
        try {
            $files = scandir($directory);
        } catch (Stagehand_LegacyError_PHPError $e) {
            Stagehand_LegacyError_PHPError::disableConversion();
            throw new Stagehand_DirectoryScanner_Exception($e->getMessage());
        }
        Stagehand_LegacyError_PHPError::disableConversion();
        if ($files === false) {
            throw new Stagehand_DirectoryScanner_Exception(
                'Failed to scan the directory [ ' .
                $directory .
                ' ], possible reasons are the directory is not found or not readable'
                                                           );
        }

        for ($i = 0, $count = count($files); $i < $count; ++$i) {
            if ($this->denyAllow->evaluate($files[$i]) == Stagehand_AccessControl_AccessState::DENY) {
                continue;
            }

            $absoluteFilePath = $directory . DIRECTORY_SEPARATOR . $files[$i];
            call_user_func($this->callback, $absoluteFilePath);

            if (is_dir($absoluteFilePath) && $this->recursivelyScans) {
                $this->scan($absoluteFilePath);
            }
        }
    }

    // }}}
    // {{{ setRecursivelyScans()

    /**
     * @param boolean $recursivelyScans
     */
    public function setRecursivelyScans($recursivelyScans)
    {
        $this->recursivelyScans = $recursivelyScans;
    }

    // }}}
    // {{{ addExclude()

    /**
     * @param string $exclude
     */
    public function addExclude($exclude)
    {
        $this->denyAllow->deny($exclude);
    }

    // }}}
    // {{{ addInclude()

    /**
     * @param string $include
     */
    public function addInclude($include)
    {
        $this->denyAllow->allow($include);
    }

    /**#@-*/

    /**#@+
     * @access protected
     */

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
