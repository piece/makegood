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
 * CompiledContainer
 *
 * This class has been auto-generated
 * by the Symfony Dependency Injection Component.
 */
class CompiledContainer extends Container
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
     * Gets the 'autotest_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %autotest_factory.class% instance.
     */
    protected function getAutotestFactoryService()
    {
        return $this->services['autotest_factory'] = $this->get('component_aware_factory_factory')->create('autotest', $this->getParameter('plugin_aware_factory.class'));
    }

    /**
     * Gets the 'cakephp.autotest' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %cakephp.autotest.class% instance.
     */
    protected function getCakephp_AutotestService()
    {
        $class = $this->getParameter('cakephp.autotest.class');

        $this->services['cakephp.autotest'] = $instance = new $class($this->get('preparer_factory'));

        $instance->setAlterationMonitoring($this->get('alteration_monitoring'));
        $instance->setLegacyProxy($this->get('legacy_proxy'));
        $instance->setWatchDirs($this->getParameter('autotest_watch_dirs'));
        $instance->setNotifierFactory($this->get('notifier_factory'));
        $instance->setOS($this->get('os'));
        $instance->setRunnerFactory($this->get('runner_factory'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));

        return $instance;
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
        $instance->setRecursive($this->getParameter('recursive'));

        return $instance;
    }

    /**
     * Gets the 'cakephp.junit_xml_reporter' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %cakephp.junit_xml_reporter.class% instance.
     */
    protected function getCakephp_JunitXmlReporterService()
    {
        $class = $this->getParameter('cakephp.junit_xml_reporter.class');

        return $this->services['cakephp.junit_xml_reporter'] = new $class();
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

        $instance->setJUnitXMLFile($this->getParameter('junit_xml_file'));
        $instance->setJUnitXMLRealtime($this->getParameter('junit_xml_realtime'));
        $instance->setDetailedProgress($this->getParameter('detailed_progress'));
        $instance->setStopOnFailure($this->getParameter('stop_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));
        $instance->setNotify($this->getParameter('notify'));
        $instance->setJUnitXMLReporterFactory($this->get('simpletest.junit_xml_reporter_factory'));

        return $instance;
    }

    /**
     * Gets the 'ciunit.autotest' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %ciunit.autotest.class% instance.
     */
    protected function getCiunit_AutotestService()
    {
        $class = $this->getParameter('ciunit.autotest.class');

        $this->services['ciunit.autotest'] = $instance = new $class($this->get('preparer_factory'));

        $instance->setAlterationMonitoring($this->get('alteration_monitoring'));
        $instance->setLegacyProxy($this->get('legacy_proxy'));
        $instance->setWatchDirs($this->getParameter('autotest_watch_dirs'));
        $instance->setNotifierFactory($this->get('notifier_factory'));
        $instance->setOS($this->get('os'));
        $instance->setRunnerFactory($this->get('runner_factory'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));
        $instance->setPHPUnitXMLConfiguration($this->get('phpunit.phpunit_xml_configuration'));

        return $instance;
    }

    /**
     * Gets the 'ciunit.collector' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %ciunit.collector.class% instance.
     */
    protected function getCiunit_CollectorService()
    {
        $class = $this->getParameter('ciunit.collector.class');

        $this->services['ciunit.collector'] = $instance = new $class($this->get('test_target_repository'));

        $instance->setCollectingTypeFactory($this->get('collecting_type_factory'));
        $instance->setRecursive($this->getParameter('recursive'));
        $instance->setPHPUnitXMLConfiguration($this->get('phpunit.phpunit_xml_configuration'));

        return $instance;
    }

    /**
     * Gets the 'ciunit.preparer' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %ciunit.preparer.class% instance.
     */
    protected function getCiunit_PreparerService()
    {
        $class = $this->getParameter('ciunit.preparer.class');

        $this->services['ciunit.preparer'] = $instance = new $class();

        $instance->setPHPUnitXMLConfiguration($this->get('phpunit.phpunit_xml_configuration'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setCIUnitPath($this->getParameter('ciunit.ciunit_path'));

        return $instance;
    }

    /**
     * Gets the 'ciunit.runner' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %ciunit.runner.class% instance.
     */
    protected function getCiunit_RunnerService()
    {
        $class = $this->getParameter('ciunit.runner.class');

        $this->services['ciunit.runner'] = $instance = new $class();

        $instance->setJUnitXMLFile($this->getParameter('junit_xml_file'));
        $instance->setJUnitXMLRealtime($this->getParameter('junit_xml_realtime'));
        $instance->setDetailedProgress($this->getParameter('detailed_progress'));
        $instance->setStopOnFailure($this->getParameter('stop_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));
        $instance->setNotify($this->getParameter('notify'));
        $instance->setPHPUnitXMLConfiguration($this->get('phpunit.phpunit_xml_configuration'));

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
     * Gets the 'collector_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %collector_factory.class% instance.
     */
    protected function getCollectorFactoryService()
    {
        return $this->services['collector_factory'] = $this->get('component_aware_factory_factory')->create('collector', $this->getParameter('plugin_aware_factory.class'));
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
     * Gets the 'notifier_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %notifier_factory.class% instance.
     */
    protected function getNotifierFactoryService()
    {
        return $this->services['notifier_factory'] = $this->get('component_aware_factory_factory')->create('notifier');
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
     * Gets the 'phpspec.autotest' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.autotest.class% instance.
     */
    protected function getPhpspec_AutotestService()
    {
        $class = $this->getParameter('phpspec.autotest.class');

        $this->services['phpspec.autotest'] = $instance = new $class($this->get('preparer_factory'));

        $instance->setAlterationMonitoring($this->get('alteration_monitoring'));
        $instance->setLegacyProxy($this->get('legacy_proxy'));
        $instance->setWatchDirs($this->getParameter('autotest_watch_dirs'));
        $instance->setNotifierFactory($this->get('notifier_factory'));
        $instance->setOS($this->get('os'));
        $instance->setRunnerFactory($this->get('runner_factory'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));

        return $instance;
    }

    /**
     * Gets the 'phpspec.collector' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.collector.class% instance.
     */
    protected function getPhpspec_CollectorService()
    {
        $class = $this->getParameter('phpspec.collector.class');

        $this->services['phpspec.collector'] = $instance = new $class($this->get('test_target_repository'));

        $instance->setCollectingTypeFactory($this->get('collecting_type_factory'));
        $instance->setRecursive($this->getParameter('recursive'));

        return $instance;
    }

    /**
     * Gets the 'phpspec.preparer' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.preparer.class% instance.
     */
    protected function getPhpspec_PreparerService()
    {
        $class = $this->getParameter('phpspec.preparer.class');

        return $this->services['phpspec.preparer'] = new $class();
    }

    /**
     * Gets the 'phpspec.runner' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.runner.class% instance.
     */
    protected function getPhpspec_RunnerService()
    {
        $class = $this->getParameter('phpspec.runner.class');

        $this->services['phpspec.runner'] = $instance = new $class();

        $instance->setJUnitXMLFile($this->getParameter('junit_xml_file'));
        $instance->setJUnitXMLRealtime($this->getParameter('junit_xml_realtime'));
        $instance->setDetailedProgress($this->getParameter('detailed_progress'));
        $instance->setStopOnFailure($this->getParameter('stop_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));
        $instance->setNotify($this->getParameter('notify'));

        return $instance;
    }

    /**
     * Gets the 'phpunit.autotest' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpunit.autotest.class% instance.
     */
    protected function getPhpunit_AutotestService()
    {
        $class = $this->getParameter('phpunit.autotest.class');

        $this->services['phpunit.autotest'] = $instance = new $class($this->get('preparer_factory'));

        $instance->setAlterationMonitoring($this->get('alteration_monitoring'));
        $instance->setLegacyProxy($this->get('legacy_proxy'));
        $instance->setWatchDirs($this->getParameter('autotest_watch_dirs'));
        $instance->setNotifierFactory($this->get('notifier_factory'));
        $instance->setOS($this->get('os'));
        $instance->setRunnerFactory($this->get('runner_factory'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));
        $instance->setPHPUnitXMLConfiguration($this->get('phpunit.phpunit_xml_configuration'));

        return $instance;
    }

    /**
     * Gets the 'phpunit.collector' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpunit.collector.class% instance.
     */
    protected function getPhpunit_CollectorService()
    {
        $class = $this->getParameter('phpunit.collector.class');

        $this->services['phpunit.collector'] = $instance = new $class($this->get('test_target_repository'));

        $instance->setCollectingTypeFactory($this->get('collecting_type_factory'));
        $instance->setRecursive($this->getParameter('recursive'));
        $instance->setPHPUnitXMLConfiguration($this->get('phpunit.phpunit_xml_configuration'));

        return $instance;
    }

    /**
     * Gets the 'phpunit.phpunit_xml_configuration' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpunit.phpunit_xml_configuration.class% instance.
     */
    protected function getPhpunit_PhpunitXmlConfigurationService()
    {
        $class = $this->getParameter('phpunit.phpunit_xml_configuration.class');

        $this->services['phpunit.phpunit_xml_configuration'] = $instance = new $class();

        $instance->setFileName($this->getParameter('phpunit.phpunit_config_file'));

        return $instance;
    }

    /**
     * Gets the 'phpunit.preparer' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpunit.preparer.class% instance.
     */
    protected function getPhpunit_PreparerService()
    {
        $class = $this->getParameter('phpunit.preparer.class');

        $this->services['phpunit.preparer'] = $instance = new $class();

        $instance->setPHPUnitXMLConfiguration($this->get('phpunit.phpunit_xml_configuration'));
        $instance->setTerminal($this->get('terminal'));

        return $instance;
    }

    /**
     * Gets the 'phpunit.runner' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpunit.runner.class% instance.
     */
    protected function getPhpunit_RunnerService()
    {
        $class = $this->getParameter('phpunit.runner.class');

        $this->services['phpunit.runner'] = $instance = new $class();

        $instance->setJUnitXMLFile($this->getParameter('junit_xml_file'));
        $instance->setJUnitXMLRealtime($this->getParameter('junit_xml_realtime'));
        $instance->setDetailedProgress($this->getParameter('detailed_progress'));
        $instance->setStopOnFailure($this->getParameter('stop_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));
        $instance->setNotify($this->getParameter('notify'));
        $instance->setPHPUnitXMLConfiguration($this->get('phpunit.phpunit_xml_configuration'));

        return $instance;
    }

    /**
     * Gets the 'preparer_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %preparer_factory.class% instance.
     */
    protected function getPreparerFactoryService()
    {
        return $this->services['preparer_factory'] = $this->get('component_aware_factory_factory')->create('preparer', $this->getParameter('plugin_aware_factory.class'));
    }

    /**
     * Gets the 'runner_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %runner_factory.class% instance.
     */
    protected function getRunnerFactoryService()
    {
        return $this->services['runner_factory'] = $this->get('component_aware_factory_factory')->create('runner', $this->getParameter('plugin_aware_factory.class'));
    }

    /**
     * Gets the 'simpletest.autotest' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %simpletest.autotest.class% instance.
     */
    protected function getSimpletest_AutotestService()
    {
        $class = $this->getParameter('simpletest.autotest.class');

        $this->services['simpletest.autotest'] = $instance = new $class($this->get('preparer_factory'));

        $instance->setAlterationMonitoring($this->get('alteration_monitoring'));
        $instance->setLegacyProxy($this->get('legacy_proxy'));
        $instance->setWatchDirs($this->getParameter('autotest_watch_dirs'));
        $instance->setNotifierFactory($this->get('notifier_factory'));
        $instance->setOS($this->get('os'));
        $instance->setRunnerFactory($this->get('runner_factory'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));

        return $instance;
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
        return $this->services['simpletest.junit_xml_reporter_factory'] = $this->get('component_aware_factory_factory')->create('junit_xml_reporter', $this->getParameter('simpletest.junit_xml_reporter_factory.class'));
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

        $instance->setJUnitXMLFile($this->getParameter('junit_xml_file'));
        $instance->setJUnitXMLRealtime($this->getParameter('junit_xml_realtime'));
        $instance->setDetailedProgress($this->getParameter('detailed_progress'));
        $instance->setStopOnFailure($this->getParameter('stop_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargetRepository($this->get('test_target_repository'));
        $instance->setNotify($this->getParameter('notify'));
        $instance->setJUnitXMLReporterFactory($this->get('simpletest.junit_xml_reporter_factory'));

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
     * Gets the 'test_run' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %test_run.class% instance.
     */
    protected function getTestRunService()
    {
        $class = $this->getParameter('test_run.class');

        $this->services['test_run'] = $instance = new $class();

        $instance->setCollectorFactory($this->get('collector_factory'));
        $instance->setNotifierFactory($this->get('notifier_factory'));
        $instance->setPreparerFactory($this->get('preparer_factory'));
        $instance->setRunnerFactory($this->get('runner_factory'));
        $instance->setOutputBuffering($this->get('output_buffering'));

        return $instance;
    }

    /**
     * Gets the 'test_run_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %test_run_factory.class% instance.
     */
    protected function getTestRunFactoryService()
    {
        return $this->services['test_run_factory'] = $this->get('component_aware_factory_factory')->create('test_run');
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

        $this->services['test_runner'] = $instance = new $class();

        $instance->setAutotestFactory($this->get('autotest_factory'));
        $instance->setAutotest($this->getParameter('autotest'));
        $instance->setTestRunFactory($this->get('test_run_factory'));

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

        $this->services['test_target_repository'] = $instance = new $class();

        $instance->setClasses($this->getParameter('test_classes'));
        $instance->setFilePattern($this->getParameter('test_file_pattern'));
        $instance->setMethods($this->getParameter('test_methods'));
        $instance->setResources($this->getParameter('test_resources'));

        return $instance;
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
            'plugin_aware_factory.class' => 'Stagehand\\TestRunner\\Core\\Plugin\\PluginAwareFactory',
            'alteration_monitoring.class' => 'Stagehand\\TestRunner\\Process\\AlterationMonitoring',
            'autotest_factory.class' => 'Stagehand\\TestRunner\\Process\\Autotest\\Autotest',
            'collecting_type.class' => 'Stagehand\\TestRunner\\Collector\\CollectingType',
            'collecting_type_factory.class' => 'Stagehand\\TestRunner\\Collector\\CollectingTypeFactory',
            'collector_factory.class' => 'Stagehand\\TestRunner\\Collector\\Collector',
            'legacy_proxy.class' => 'Stagehand\\TestRunner\\Util\\LegacyProxy',
            'notifier.class' => 'Stagehand\\TestRunner\\Notification\\Notifier',
            'notifier_factory.class' => 'Stagehand\\TestRunner\\Notification\\Notifier',
            'os.class' => 'Stagehand\\TestRunner\\Util\\OS',
            'output_buffering.class' => 'Stagehand\\TestRunner\\Util\\OutputBuffering',
            'preparer_factory.class' => 'Stagehand\\TestRunner\\Preparer\\Preparer',
            'runner_factory.class' => 'Stagehand\\TestRunner\\Runner\\Runner',
            'terminal.class' => 'Stagehand\\TestRunner\\CLI\\Terminal',
            'test_run.class' => 'Stagehand\\TestRunner\\Process\\TestRun',
            'test_run_factory.class' => 'Stagehand\\TestRunner\\Process\\TestRun',
            'test_runner.class' => 'Stagehand\\TestRunner\\CLI\\TestRunner',
            'test_target_repository.class' => 'Stagehand\\TestRunner\\Core\\TestTargetRepository',
            'recursive' => false,
            'autotest' => false,
            'autotest_watch_dirs' => array(

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
            'ciunit.autotest.class' => 'Stagehand\\TestRunner\\Process\\Autotest\\CIUnitAutotest',
            'ciunit.collector.class' => 'Stagehand\\TestRunner\\Collector\\CIUnitCollector',
            'ciunit.preparer.class' => 'Stagehand\\TestRunner\\Preparer\\CIUnitPreparer',
            'ciunit.runner.class' => 'Stagehand\\TestRunner\\Runner\\CIUnitRunner',
            'ciunit.ciunit_path' => NULL,
            'cakephp.autotest.class' => 'Stagehand\\TestRunner\\Process\\Autotest\\CakePHPAutotest',
            'cakephp.collector.class' => 'Stagehand\\TestRunner\\Collector\\CakePHPCollector',
            'cakephp.junit_xml_reporter.class' => 'Stagehand\\TestRunner\\Runner\\CakePHPRunner\\JUnitXMLReporter',
            'cakephp.preparer.class' => 'Stagehand\\TestRunner\\Preparer\\CakePHPPreparer',
            'cakephp.runner.class' => 'Stagehand\\TestRunner\\Runner\\CakePHPRunner',
            'cakephp.cakephp_app_path' => NULL,
            'cakephp.cakephp_core_path' => NULL,
            'phpspec.autotest.class' => 'Stagehand\\TestRunner\\Process\\Autotest\\PHPSpecAutotest',
            'phpspec.collector.class' => 'Stagehand\\TestRunner\\Collector\\PHPSpecCollector',
            'phpspec.preparer.class' => 'Stagehand\\TestRunner\\Preparer\\PHPSpecPreparer',
            'phpspec.runner.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner',
            'phpunit.autotest.class' => 'Stagehand\\TestRunner\\Process\\Autotest\\PHPUnitAutotest',
            'phpunit.collector.class' => 'Stagehand\\TestRunner\\Collector\\PHPUnitCollector',
            'phpunit.phpunit_xml_configuration.class' => 'Stagehand\\TestRunner\\Util\\PHPUnitXMLConfiguration',
            'phpunit.preparer.class' => 'Stagehand\\TestRunner\\Preparer\\PHPUnitPreparer',
            'phpunit.runner.class' => 'Stagehand\\TestRunner\\Runner\\PHPUnitRunner',
            'phpunit.phpunit_config_file' => NULL,
            'simpletest.autotest.class' => 'Stagehand\\TestRunner\\Process\\Autotest\\SimpleTestAutotest',
            'simpletest.collector.class' => 'Stagehand\\TestRunner\\Collector\\SimpleTestCollector',
            'simpletest.junit_xml_reporter.class' => 'Stagehand\\TestRunner\\Runner\\SimpleTestRunner\\JUnitXMLReporter',
            'simpletest.junit_xml_reporter_factory.class' => 'Stagehand\\TestRunner\\Runner\\SimpleTestRunner\\JUnitXMLReporterFactory',
            'simpletest.preparer.class' => 'Stagehand\\TestRunner\\Preparer\\SimpleTestPreparer',
            'simpletest.runner.class' => 'Stagehand\\TestRunner\\Runner\\SimpleTestRunner',
        );
    }
}
