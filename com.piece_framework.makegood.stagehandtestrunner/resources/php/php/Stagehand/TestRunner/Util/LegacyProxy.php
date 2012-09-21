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
 * @version    Release: 3.3.1
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\Util;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.3.1
 * @since      Class available since Release 3.0.0
 */
class LegacyProxy
{
    /**
     * @param string $command
     * @return integer
     */
    public function passthru($command)
    {
        passthru($command, $exitStatus);
        return $exitStatus;
    }

    /**
     * @param string $option
     * @return string
     */
    public function get_cfg_var($option)
    {
        return get_cfg_var('cfg_file_path');
    }

    /**
     * @param string $filename
     * @return boolean
     */
    public function is_dir($filename)
    {
        return is_dir($filename);
    }

    /**
     * @param string $path
     * @return string
     */
    public function realpath($path)
    {
        return realpath($path);
    }

    /**
     * @param mixed $object
     * @param string $class_name
     * @return boolean
     */
    public function is_subclass_of($object, $class_name)
    {
        return is_subclass_of($object, $class_name);
    }

    /**
     * @return string
     */
    public function PHP_OS()
    {
        return PHP_OS;
    }

    /**
     * @param string $command
     * @param string $return_var
     * @return string
     */
    public function system($command, &$return_var)
    {
        return system($command, $return_var);
    }

    /**
     * @return integer
     */
    public function ob_get_level()
    {
        return ob_get_level();
    }

    /**
     * @return boolean
     */
    public function ob_end_clean()
    {
        return ob_end_clean();
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
