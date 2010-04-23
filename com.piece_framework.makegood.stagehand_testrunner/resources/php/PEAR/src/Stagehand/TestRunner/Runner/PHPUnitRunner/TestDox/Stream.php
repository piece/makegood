<?php
/* vim: set expandtab tabstop=4 shiftwidth=4: */

/**
 * PHP version 5
 *
 * Copyright (c) 2008-2010 KUBO Atsuhiro <kubo@iteman.jp>,
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
 * @copyright  2008-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.1
 * @link       http://www.php.net/manual/ja/function.stream-wrapper-register.php
 * @since      File available since Release 2.4.0
 */

/**
 * A stream wrapper to print TestDox documentation.
 *
 * @package    Stagehand_TestRunner
 * @copyright  2008-2010 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 2.11.1
 * @link       http://www.php.net/manual/ja/function.stream-wrapper-register.php
 * @since      Class available since Release 2.4.0
 */
class Stagehand_TestRunner_Runner_PHPUnitRunner_TestDox_Stream
{
    protected $position = 0;
    protected $resultID;

    /**
     * The implementation of stream_open().
     *
     * @param string  $path
     * @param string  $mode
     * @param integer $options
     * @param string  &$opened_path
     * @return boolean
     */
    public function stream_open($path, $mode, $options, &$opened_path)
    {
        preg_match('!^testdox://(.+)$!', $path, $matches);
        $this->resultID = $matches[1];
        Stagehand_TestRunner_Runner_PHPUnitRunner_TestDox::initialize($this->resultID);
        $opened_path = $path;
        return true;
    }

    /**
     * The implementation of stream_close().
     *
     * @param string  $path
     * @param string  $mode
     * @param integer $options
     * @param string  $opened_path
     * @return boolean
     */
    public function stream_close() {}

    /**
     * The implementation of stream_read().
     *
     * @param integer $count
     * @return string
     */
    public function stream_read($count)
    {
        $data = substr($this->getTestDox(), $this->position, $count);
        $this->position += strlen($data);
        return $data;
    }

    /**
     * The implementation of stream_write().
     *
     * @param string $data
     * @return integer
     */
    public function stream_write($data)
    {
        $this->appendTestDox($data);
        $this->position += strlen($data);
        return strlen($data);
    }

    /**
     * The implementation of stream_eof().
     *
     * @return boolean
     */
    public function stream_eof()
    {
        return $this->position >= strlen($this->getTestDox());
    }

    /**
     * The implementation of stream_tell().
     *
     * @return integer
     */
    public function stream_tell()
    {
        return $this->position;
    }

    /**
     * The implementation of stream_seek().
     *
     * @param integer $offset
     * @param integer $whence
     * @return boolean
     */
    public function stream_seek($offset, $whence)
    {
        switch ($whence) {
        case SEEK_SET:
            if ($offset < strlen($this->getTestDox()) && $offset >= 0) {
                $this->position = $offset;
                return true;
            } else {
                return false;
            }
        case SEEK_CUR:
            if ($offset >= 0) {
                $this->position += $offset;
                return true;
            } else {
                return false;
            }
        case SEEK_END:
            if (strlen($this->getTestDox()) + $offset >= 0) {
                $this->position = strlen($this->getTestDox()) + $offset;
                return true;
            } else {
                return false;
            }
        default:
            return false;
        }        
    }

    public static function register()
    {
        if (!in_array('testdox', stream_get_wrappers())) {
            stream_wrapper_register('testdox',  __CLASS__);
        }
    }

    /**
     * @return string
     * @since Method available since Release 2.10.0
     */
    protected function getTestDox()
    {
        return Stagehand_TestRunner_Runner_PHPUnitRunner_TestDox::get($this->resultID);
    }

    /**
     * @param string $testDox
     * @since Method available since Release 2.10.0
     */
    protected function appendTestDox($testDox)
    {
        Stagehand_TestRunner_Runner_PHPUnitRunner_TestDox::append(
            $this->resultID, $testDox
        );
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
