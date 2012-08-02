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
 * @version    Release: 3.2.0
 * @since      File available since Release 2.20.0
 */

namespace Stagehand\TestRunner\Collector;

use Stagehand\TestRunner\Util\LegacyProxy;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2011-2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.2.0
 * @since      Class available since Release 2.20.0
 */
class CollectingType
{
    protected $type;
    protected $requiredSuperTypes = array();

    /**
     * @var \Stagehand\TestRunner\Util\LegacyProxy
     * @since Property available since Release 3.0.0
     */
    protected $legacyProxy;

    /**
     * @return boolean
     */
    public function isTest()
    {
        if (in_array($this->type, $this->requiredSuperTypes)) {
            return false;
        } else {
            foreach ($this->requiredSuperTypes as $expectedSuperType) {
                if ($this->isSubTypeOfRequiredSuperType($this->type, $expectedSuperType)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * @param \Stagehand\TestRunner\Util\LegacyProxy $legacyProxy
     * @since Method available since Release 3.0.0
     */
    public function setLegacyProxy(LegacyProxy $legacyProxy)
    {
        $this->legacyProxy = $legacyProxy;
    }

    /**
     * @param string $type
     * @since Method available since Release 3.0.0
     */
    public function setType($type)
    {
        $this->type = $type;
    }

    /**
     * @param array $requiredSuperTypes
     * @since Method available since Release 3.0.0
     */
    public function setRequiredSuperTypes(array $requiredSuperTypes)
    {
        $this->requiredSuperTypes = $requiredSuperTypes;
    }

    /**
     * @param string $type
     * @param string $expectedSuperType
     */
    protected function isSubTypeOfRequiredSuperType($type, $expectedSuperType)
    {
        return $this->legacyProxy->is_subclass_of($type, $expectedSuperType);
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
