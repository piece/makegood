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
 * @package    Stagehand_TestRunner
 * @copyright  2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.2.0
 * @since      File available since Release 3.0.0
 */

namespace Stagehand\TestRunner\Core\Transformation;

use Stagehand\TestRunner\Core\Configuration\GeneralConfiguration;

/**
 * @package    Stagehand_TestRunner
 * @copyright  2012 KUBO Atsuhiro <kubo@iteman.jp>
 * @license    http://www.opensource.org/licenses/bsd-license.php  New BSD License
 * @version    Release: 3.2.0
 * @since      Class available since Release 3.0.0
 */
class GeneralTransformer extends Transformer
{
    public function transform()
    {
        $this->setParameter('test_resources', $this->configurationPart['test_targets']['resources']);
        $this->setParameter('recursively_scans', $this->configurationPart['test_targets']['recursive']);
        $this->setParameter('test_methods', $this->configurationPart['test_targets']['methods']);
        $this->setParameter('test_classes', $this->configurationPart['test_targets']['classes']);
        $this->setParameter('test_file_pattern', $this->configurationPart['test_targets']['file_pattern']);

        $this->setParameter('enables_autotest', $this->configurationPart['autotest']['enabled']);
        $this->setParameter('monitoring_directories', $this->configurationPart['autotest']['watch_dirs']);

        $this->setParameter('uses_notification', $this->configurationPart['notify']);

        $this->setParameter('junit_xml_file', $this->configurationPart['junit_xml']['file']);
        $this->setParameter('logs_results_in_junit_xml_in_realtime', $this->configurationPart['junit_xml']['realtime']);

        $this->setParameter('stops_on_failure', $this->configurationPart['stop_on_failure']);
        $this->setParameter('prints_detailed_progress_report', $this->configurationPart['detailed_progress']);
    }

    protected function createConfiguration()
    {
        return new GeneralConfiguration();
    }

    protected function getParameterPrefix()
    {
        return '';
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
