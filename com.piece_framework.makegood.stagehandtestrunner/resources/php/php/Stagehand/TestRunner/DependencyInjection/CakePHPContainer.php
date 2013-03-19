<?php
namespace Stagehand\TestRunner\DependencyInjection;


use Symfony\Component\DependencyInjection\ContainerInterface;
use Symfony\Component\DependencyInjection\Container;
use Symfony\Component\DependencyInjection\Exception\InactiveScopeException;
use Symfony\Component\DependencyInjection\Exception\InvalidArgumentException;
use Symfony\Component\DependencyInjection\Exception\LogicException;
use Symfony\Component\DependencyInjection\Exception\RuntimeException;
use Symfony\Component\DependencyInjection\Reference;
use Symfony\Component\DependencyInjection\Parameter;
use Symfony\Component\DependencyInjection\ParameterBag\ParameterBag;

/**
 * CakePHPContainer
 *
 * This class has been auto-generated
 * by the Symfony Dependency Injection Component.
 */
class CakePHPContainer extends Container
{
    /**
     * Constructor.
     */
    public function __construct()
    {
        parent::__construct(new ParameterBag($this->getDefaultParameters()));
    }

    /**
     * Gets the 'alteration_monitoring' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %alteration_monitoring.class% instance.
     */
    protected function getAlterationMonitoringService()
    {
        $class = $this->getParameter('alteration_monitoring.class');

        return $this->services['alteration_monitoring'] = new $class();
    }

    /**
     * Gets the 'cakephp.collector' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %cakephp.collector.class% instance.
     */
    protected function getCakephp_CollectorService()
    {
        $class = $this->getParameter('cakephp.collector.class');

        $this->services['cakephp.collector'] = $instance = new $class($this->get('test_target_repository'));

        $instance->setCollectingTypeFactory($this->get('collecting_type_factory'));
        $instance->setEnvironment($this->get('environment'));
        $instance->setRecursive($this->getParameter('recursive'));

        return $instance;
    }

    /**
     * Gets the 'cakephp.command_line_option_builder' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %cakephp.command_line_option_builder.class% instance.
     */
    protected function getCakephp_CommandLineOptionBuilderService()
    {
        $class = $this->getParameter('cakephp.command_line_option_builder.class');

        return $this->services['cakephp.command_line_option_builder'] = new $class($this->get('cakephp.preparer'));
    }

    /**
     * Gets the 'cakephp.preparer' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %cakephp.preparer.class% instance.
     */
    protected function getCakephp_PreparerService()
    {
        $class = $this->getParameter('cakephp.preparer.class');

        $this->services['cakephp.preparer'] = $instance = new $class();

        $instance->setCakePHPAppPath($this->getParameter('cakephp.cakephp_app_path'));
        $instance->setCakePHPCorePath($this->getParameter('cakephp.cakephp_core_path'));

        return $instance;
    }

    /**
     * Gets the 'cakephp.runner' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %cakephp.runner.class% instance.
     */
    protected function getCakephp_RunnerService()
    {
        $class = $this->getParameter('cakephp.runner.class');

        $this->services['cakephp.runner'] = $instance = new $class();

        $instance->setDetailedProgress($this->getParameter('detailed_progress'));
        $instance->setJUnitXMLFile($this->getParameter('junit_xml_file'));
        $instance->setJUnitXMLRealtime($this->getParameter('junit_xml_realtime'));
        $instance->setJUnitXMLReporterFactory($this->get('simpletest.junit_xml_reporter_factory'));
        $instance->setNotify($this->getParameter('notify'));
        $instance->setStopOnFailure($this->getParameter('stop_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));

        return $instance;
    }

    /**
     * Gets the 'collecting_type' service.
     *
     * @return Object A %collecting_type.class% instance.
     */
    protected function getCollectingTypeService()
    {
        $class = $this->getParameter('collecting_type.class');

        $instance = new $class();

        $instance->setLegacyProxy($this->get('legacy_proxy'));

        return $instance;
    }

    /**
     * Gets the 'collecting_type_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %collecting_type_factory.class% instance.
     */
    protected function getCollectingTypeFactoryService()
    {
        return $this->services['collecting_type_factory'] = $this->get('component_aware_factory_factory')->create('collecting_type', $this->getParameter('collecting_type_factory.class'));
    }

    /**
     * Gets the 'command_line_builder' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %command_line_builder.class% instance.
     */
    protected function getCommandLineBuilderService()
    {
        $class = $this->getParameter('command_line_builder.class');

        return $this->services['command_line_builder'] = new $class($this->get('environment'), $this->get('legacy_proxy'), $this->get('os'), $this->get('plugin'), $this->get('cakephp.runner'), $this->get('terminal'), $this->get('test_target_repository'), $this->get('cakephp.command_line_option_builder'));
    }

    /**
     * Gets the 'component_aware_factory_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %component_aware_factory_factory.class% instance.
     */
    protected function getComponentAwareFactoryFactoryService()
    {
        $class = $this->getParameter('component_aware_factory_factory.class');

        $this->services['component_aware_factory_factory'] = $instance = new $class();

        $instance->setFactoryClass($this->getParameter('component_aware_factory.class'));
        $instance->setComponentFactory($this->get('component_factory'));

        return $instance;
    }

    /**
     * Gets the 'component_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @throws RuntimeException always since this service is expected to be injected dynamically
     */
    protected function getComponentFactoryService()
    {
        throw new RuntimeException('You have requested a synthetic service ("component_factory"). The DIC does not know how to construct this service.');
    }

    /**
     * Gets the 'continuous_test_runner' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %continuous_test_runner.class% instance.
     */
    protected function getContinuousTestRunnerService()
    {
        $class = $this->getParameter('continuous_test_runner.class');

        $this->services['continuous_test_runner'] = $instance = new $class($this->get('cakephp.preparer'), $this->get('command_line_builder'));

        $instance->setAlterationMonitoring($this->get('alteration_monitoring'));
        $instance->setLegacyProxy($this->get('legacy_proxy'));
        $instance->setNotifier($this->get('notifier'));
        $instance->setOS($this->get('os'));
        $instance->setRunner($this->get('cakephp.runner'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));
        $instance->setWatchDirs($this->getParameter('continuous_testing_watch_dirs'));

        return $instance;
    }

    /**
     * Gets the 'environment' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @throws RuntimeException always since this service is expected to be injected dynamically
     */
    protected function getEnvironmentService()
    {
        throw new RuntimeException('You have requested a synthetic service ("environment"). The DIC does not know how to construct this service.');
    }

    /**
     * Gets the 'input' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @throws RuntimeException always since this service is expected to be injected dynamically
     */
    protected function getInputService()
    {
        throw new RuntimeException('You have requested a synthetic service ("input"). The DIC does not know how to construct this service.');
    }

    /**
     * Gets the 'legacy_proxy' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %legacy_proxy.class% instance.
     */
    protected function getLegacyProxyService()
    {
        $class = $this->getParameter('legacy_proxy.class');

        return $this->services['legacy_proxy'] = new $class();
    }

    /**
     * Gets the 'notifier' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %notifier.class% instance.
     */
    protected function getNotifierService()
    {
        $class = $this->getParameter('notifier.class');

        $this->services['notifier'] = $instance = new $class();

        $instance->setLegacyProxy($this->get('legacy_proxy'));
        $instance->setOS($this->get('os'));

        return $instance;
    }

    /**
     * Gets the 'os' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %os.class% instance.
     */
    protected function getOsService()
    {
        $class = $this->getParameter('os.class');

        $this->services['os'] = $instance = new $class();

        $instance->setLegacyProxy($this->get('legacy_proxy'));

        return $instance;
    }

    /**
     * Gets the 'output' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @throws RuntimeException always since this service is expected to be injected dynamically
     */
    protected function getOutputService()
    {
        throw new RuntimeException('You have requested a synthetic service ("output"). The DIC does not know how to construct this service.');
    }

    /**
     * Gets the 'output_buffering' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %output_buffering.class% instance.
     */
    protected function getOutputBufferingService()
    {
        $class = $this->getParameter('output_buffering.class');

        $this->services['output_buffering'] = $instance = new $class();

        $instance->setLegacyProxy($this->get('legacy_proxy'));

        return $instance;
    }

    /**
     * Gets the 'plugin' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @throws RuntimeException always since this service is expected to be injected dynamically
     */
    protected function getPluginService()
    {
        throw new RuntimeException('You have requested a synthetic service ("plugin"). The DIC does not know how to construct this service.');
    }

    /**
     * Gets the 'simpletest.collector' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %simpletest.collector.class% instance.
     */
    protected function getSimpletest_CollectorService()
    {
        $class = $this->getParameter('simpletest.collector.class');

        $this->services['simpletest.collector'] = $instance = new $class($this->get('test_target_repository'));

        $instance->setCollectingTypeFactory($this->get('collecting_type_factory'));
        $instance->setEnvironment($this->get('environment'));
        $instance->setRecursive($this->getParameter('recursive'));

        return $instance;
    }

    /**
     * Gets the 'simpletest.junit_xml_reporter' service.
     *
     * @return Object A %simpletest.junit_xml_reporter.class% instance.
     */
    protected function getSimpletest_JunitXmlReporterService()
    {
        $class = $this->getParameter('simpletest.junit_xml_reporter.class');

        return new $class();
    }

    /**
     * Gets the 'simpletest.junit_xml_reporter_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %simpletest.junit_xml_reporter_factory.class% instance.
     */
    protected function getSimpletest_JunitXmlReporterFactoryService()
    {
        return $this->services['simpletest.junit_xml_reporter_factory'] = $this->get('component_aware_factory_factory')->create('simpletest.junit_xml_reporter', $this->getParameter('simpletest.junit_xml_reporter_factory.class'));
    }

    /**
     * Gets the 'simpletest.preparer' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %simpletest.preparer.class% instance.
     */
    protected function getSimpletest_PreparerService()
    {
        $class = $this->getParameter('simpletest.preparer.class');

        return $this->services['simpletest.preparer'] = new $class();
    }

    /**
     * Gets the 'simpletest.runner' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %simpletest.runner.class% instance.
     */
    protected function getSimpletest_RunnerService()
    {
        $class = $this->getParameter('simpletest.runner.class');

        $this->services['simpletest.runner'] = $instance = new $class();

        $instance->setDetailedProgress($this->getParameter('detailed_progress'));
        $instance->setJUnitXMLFile($this->getParameter('junit_xml_file'));
        $instance->setJUnitXMLRealtime($this->getParameter('junit_xml_realtime'));
        $instance->setJUnitXMLReporterFactory($this->get('simpletest.junit_xml_reporter_factory'));
        $instance->setNotify($this->getParameter('notify'));
        $instance->setStopOnFailure($this->getParameter('stop_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));

        return $instance;
    }

    /**
     * Gets the 'terminal' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %terminal.class% instance.
     */
    protected function getTerminalService()
    {
        $class = $this->getParameter('terminal.class');

        $this->services['terminal'] = $instance = new $class();

        $instance->setInput($this->get('input'));
        $instance->setOutput($this->get('output'));

        return $instance;
    }

    /**
     * Gets the 'test_runner' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %test_runner.class% instance.
     */
    protected function getTestRunnerService()
    {
        $class = $this->getParameter('test_runner.class');

        $this->services['test_runner'] = $instance = new $class($this->get('cakephp.preparer'));

        $instance->setCollector($this->get('cakephp.collector'));
        $instance->setNotifier($this->get('notifier'));
        $instance->setRunner($this->get('cakephp.runner'));
        $instance->setOutputBuffering($this->get('output_buffering'));

        return $instance;
    }

    /**
     * Gets the 'test_target_repository' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %test_target_repository.class% instance.
     */
    protected function getTestTargetRepositoryService()
    {
        $class = $this->getParameter('test_target_repository.class');

        $this->services['test_target_repository'] = $instance = new $class($this->get('plugin'));

        $instance->setClasses($this->getParameter('test_classes'));
        $instance->setFilePattern($this->getParameter('test_file_pattern'));
        $instance->setMethods($this->getParameter('test_methods'));
        $instance->setResources($this->getParameter('test_resources'));

        return $instance;
    }

    /**
     * Gets the collector service alias.
     *
     * @return Object An instance of the cakephp.collector service
     */
    protected function getCollectorService()
    {
        return $this->get('cakephp.collector');
    }

    /**
     * Gets the command_line_option_builder service alias.
     *
     * @return Object An instance of the cakephp.command_line_option_builder service
     */
    protected function getCommandLineOptionBuilderService()
    {
        return $this->get('cakephp.command_line_option_builder');
    }

    /**
     * Gets the preparer service alias.
     *
     * @return Object An instance of the cakephp.preparer service
     */
    protected function getPreparerService()
    {
        return $this->get('cakephp.preparer');
    }

    /**
     * Gets the runner service alias.
     *
     * @return Object An instance of the cakephp.runner service
     */
    protected function getRunnerService()
    {
        return $this->get('cakephp.runner');
    }

    /**
     * Gets the default parameters.
     *
     * @return array An array of the default parameters
     */
    protected function getDefaultParameters()
    {
        return array(
            'component_aware_factory.class' => 'Stagehand\\ComponentFactory\\ComponentAwareFactory',
            'component_aware_factory_factory.class' => 'Stagehand\\ComponentFactory\\ComponentAwareFactoryFactory',
            'alteration_monitoring.class' => 'Stagehand\\TestRunner\\Process\\ContinuousTesting\\AlterationMonitoring',
            'collecting_type.class' => 'Stagehand\\TestRunner\\Collector\\CollectingType',
            'collecting_type_factory.class' => 'Stagehand\\TestRunner\\Collector\\CollectingTypeFactory',
            'command_line_builder.class' => 'Stagehand\\TestRunner\\Process\\ContinuousTesting\\CommandLineBuilder',
            'continuous_test_runner.class' => 'Stagehand\\TestRunner\\Process\\ContinuousTesting\\ContinuousTestRunner',
            'legacy_proxy.class' => 'Stagehand\\TestRunner\\Util\\LegacyProxy',
            'notifier.class' => 'Stagehand\\TestRunner\\Notification\\Notifier',
            'os.class' => 'Stagehand\\TestRunner\\Util\\OS',
            'output_buffering.class' => 'Stagehand\\TestRunner\\Util\\OutputBuffering',
            'terminal.class' => 'Stagehand\\TestRunner\\CLI\\Terminal',
            'test_runner.class' => 'Stagehand\\TestRunner\\Process\\TestRunner',
            'test_target_repository.class' => 'Stagehand\\TestRunner\\Core\\TestTargetRepository',
            'recursive' => false,
            'continuous_testing' => false,
            'continuous_testing_watch_dirs' => array(

            ),
            'notify' => false,
            'test_methods' => array(

            ),
            'test_classes' => array(

            ),
            'junit_xml_file' => NULL,
            'junit_xml_realtime' => false,
            'stop_on_failure' => false,
            'test_file_pattern' => NULL,
            'test_resources' => array(

            ),
            'detailed_progress' => false,
            'simpletest.collector.class' => 'Stagehand\\TestRunner\\Collector\\SimpleTestCollector',
            'simpletest.junit_xml_reporter.class' => 'Stagehand\\TestRunner\\Runner\\CakePHPRunner\\JUnitXMLReporter',
            'simpletest.junit_xml_reporter_factory.class' => 'Stagehand\\TestRunner\\Runner\\SimpleTestRunner\\JUnitXMLReporterFactory',
            'simpletest.preparer.class' => 'Stagehand\\TestRunner\\Preparer\\SimpleTestPreparer',
            'simpletest.runner.class' => 'Stagehand\\TestRunner\\Runner\\SimpleTestRunner',
            'cakephp.collector.class' => 'Stagehand\\TestRunner\\Collector\\CakePHPCollector',
            'cakephp.command_line_option_builder.class' => 'Stagehand\\TestRunner\\Process\\ContinuousTesting\\CakePHPCommandLineOptionBuilder',
            'cakephp.preparer.class' => 'Stagehand\\TestRunner\\Preparer\\CakePHPPreparer',
            'cakephp.runner.class' => 'Stagehand\\TestRunner\\Runner\\CakePHPRunner',
            'cakephp.cakephp_app_path' => NULL,
            'cakephp.cakephp_core_path' => NULL,
        );
    }
}
