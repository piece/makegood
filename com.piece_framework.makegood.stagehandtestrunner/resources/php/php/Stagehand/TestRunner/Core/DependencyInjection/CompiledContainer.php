<?php
namespace Stagehand\TestRunner\Core\DependencyInjection;


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
        $instance->setMonitoringDirectories($this->getParameter('monitoring_directories'));
        $instance->setNotifierFactory($this->get('notifier_factory'));
        $instance->setOS($this->get('os'));
        $instance->setRunnerFactory($this->get('runner_factory'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargets($this->get('test_targets'));

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
        $this->services['cakephp.collector'] = $instance = new $class($this->get('test_targets'));

        $instance->setCollectingTypeFactory($this->get('collecting_type_factory'));

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
        $instance->setPrintsDetailedProgressReport($this->getParameter('prints_detailed_progress_report'));
        $instance->setStopsOnFailure($this->getParameter('stops_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargets($this->get('test_targets'));
        $instance->setUsesNotification($this->getParameter('uses_notification'));
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
        $instance->setMonitoringDirectories($this->getParameter('monitoring_directories'));
        $instance->setNotifierFactory($this->get('notifier_factory'));
        $instance->setOS($this->get('os'));
        $instance->setRunnerFactory($this->get('runner_factory'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargets($this->get('test_targets'));
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
        $this->services['ciunit.collector'] = $instance = new $class($this->get('test_targets'));

        $instance->setCollectingTypeFactory($this->get('collecting_type_factory'));
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
        $instance->setPrintsDetailedProgressReport($this->getParameter('prints_detailed_progress_report'));
        $instance->setStopsOnFailure($this->getParameter('stops_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargets($this->get('test_targets'));
        $instance->setUsesNotification($this->getParameter('uses_notification'));
        $instance->setPHPUnitXMLConfiguration($this->get('phpunit.phpunit_xml_configuration'));
        $instance->setJUnitXMLPrinterFactory($this->get('phpunit.junit_xml_printer_factory'));

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
     * Gets the 'junit_xml_dom_writer' service.
     *
     * @return Object A %junit_xml_dom_writer.class% instance.
     */
    protected function getJunitXmlDomWriterService()
    {
        $class = $this->getParameter('junit_xml_dom_writer.class');
        return new $class();
    }

    /**
     * Gets the 'junit_xml_dom_writer_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %junit_xml_dom_writer_factory.class% instance.
     */
    protected function getJunitXmlDomWriterFactoryService()
    {
        return $this->services['junit_xml_dom_writer_factory'] = $this->get('component_aware_factory_factory')->create('junit_xml_dom_writer');
    }

    /**
     * Gets the 'junit_xml_stream_writer' service.
     *
     * @return Object A %junit_xml_stream_writer.class% instance.
     */
    protected function getJunitXmlStreamWriterService()
    {
        $class = $this->getParameter('junit_xml_stream_writer.class');
        return new $class();
    }

    /**
     * Gets the 'junit_xml_stream_writer_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %junit_xml_stream_writer_factory.class% instance.
     */
    protected function getJunitXmlStreamWriterFactoryService()
    {
        return $this->services['junit_xml_stream_writer_factory'] = $this->get('component_aware_factory_factory')->create('junit_xml_stream_writer');
    }

    /**
     * Gets the 'junit_xml_writer_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %junit_xml_writer_factory.class% instance.
     */
    protected function getJunitXmlWriterFactoryService()
    {
        $class = $this->getParameter('junit_xml_writer_factory.class');
        $this->services['junit_xml_writer_factory'] = $instance = new $class();

        $instance->setLogsResultsInRealtime($this->getParameter('logs_results_in_junit_xml_in_realtime'));
        $instance->setJUnitXMLDOMWriterFactory($this->get('junit_xml_dom_writer_factory'));
        $instance->setJUnitXMLStreamWriterFactory($this->get('junit_xml_stream_writer_factory'));

        return $instance;
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
        $instance->setMonitoringDirectories($this->getParameter('monitoring_directories'));
        $instance->setNotifierFactory($this->get('notifier_factory'));
        $instance->setOS($this->get('os'));
        $instance->setRunnerFactory($this->get('runner_factory'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargets($this->get('test_targets'));

        return $instance;
    }

    /**
     * Gets the 'phpspec.cli_runner' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.cli_runner.class% instance.
     */
    protected function getPhpspec_CliRunnerService()
    {
        $class = $this->getParameter('phpspec.cli_runner.class');
        $this->services['phpspec.cli_runner'] = $instance = new $class();

        $instance->setLoader($this->get('phpspec.spec_loader_factory'));
        $instance->setExampleRunner($this->get('phpspec.example_runner'));

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
        $this->services['phpspec.collector'] = $instance = new $class($this->get('test_targets'));

        $instance->setCollectingTypeFactory($this->get('collecting_type_factory'));

        return $instance;
    }

    /**
     * Gets the 'phpspec.documentation_formatter' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.documentation_formatter.class% instance.
     */
    protected function getPhpspec_DocumentationFormatterService()
    {
        $class = $this->getParameter('phpspec.documentation_formatter.class');
        return $this->services['phpspec.documentation_formatter'] = new $class($this->get('phpspec.reporter'));
    }

    /**
     * Gets the 'phpspec.documentation_formatter_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.documentation_formatter_factory.class% instance.
     */
    protected function getPhpspec_DocumentationFormatterFactoryService()
    {
        return $this->services['phpspec.documentation_formatter_factory'] = $this->get('component_aware_factory_factory')->create('documentation_formatter', $this->getParameter('plugin_aware_factory.class'));
    }

    /**
     * Gets the 'phpspec.example_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.example_factory.class% instance.
     */
    protected function getPhpspec_ExampleFactoryService()
    {
        $class = $this->getParameter('phpspec.example_factory.class');
        $this->services['phpspec.example_factory'] = $instance = new $class();

        $instance->setTestTargets($this->get('test_targets'));

        return $instance;
    }

    /**
     * Gets the 'phpspec.example_runner' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.example_runner.class% instance.
     */
    protected function getPhpspec_ExampleRunnerService()
    {
        $class = $this->getParameter('phpspec.example_runner.class');
        $this->services['phpspec.example_runner'] = $instance = new $class();

        $instance->setExampleFactory($this->get('phpspec.example_factory'));

        return $instance;
    }

    /**
     * Gets the 'phpspec.junit_xml_formatter' service.
     *
     * @return Object A %phpspec.junit_xml_formatter.class% instance.
     */
    protected function getPhpspec_JunitXmlFormatterService()
    {
        $class = $this->getParameter('phpspec.junit_xml_formatter.class');
        $instance = new $class($this->get('phpspec.reporter'));

        $instance->setTestTargets($this->get('test_targets'));

        return $instance;
    }

    /**
     * Gets the 'phpspec.junit_xml_formatter_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.junit_xml_formatter_factory.class% instance.
     */
    protected function getPhpspec_JunitXmlFormatterFactoryService()
    {
        $this->services['phpspec.junit_xml_formatter_factory'] = $instance = $this->get('component_aware_factory_factory')->create('junit_xml_formatter', $this->getParameter('phpspec.junit_xml_formatter_factory.class'));

        $instance->setJUnitXMLWriterFactory($this->get('junit_xml_writer_factory'));

        return $instance;
    }

    /**
     * Gets the 'phpspec.notification_formatter' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.notification_formatter.class% instance.
     */
    protected function getPhpspec_NotificationFormatterService()
    {
        $class = $this->getParameter('phpspec.notification_formatter.class');
        return $this->services['phpspec.notification_formatter'] = new $class($this->get('phpspec.reporter'));
    }

    /**
     * Gets the 'phpspec.notification_formatter_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.notification_formatter_factory.class% instance.
     */
    protected function getPhpspec_NotificationFormatterFactoryService()
    {
        return $this->services['phpspec.notification_formatter_factory'] = $this->get('component_aware_factory_factory')->create('notification_formatter', $this->getParameter('plugin_aware_factory.class'));
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
     * Gets the 'phpspec.progress_formatter' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.progress_formatter.class% instance.
     */
    protected function getPhpspec_ProgressFormatterService()
    {
        $class = $this->getParameter('phpspec.progress_formatter.class');
        return $this->services['phpspec.progress_formatter'] = new $class($this->get('phpspec.reporter'));
    }

    /**
     * Gets the 'phpspec.progress_formatter_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.progress_formatter_factory.class% instance.
     */
    protected function getPhpspec_ProgressFormatterFactoryService()
    {
        return $this->services['phpspec.progress_formatter_factory'] = $this->get('component_aware_factory_factory')->create('progress_formatter', $this->getParameter('plugin_aware_factory.class'));
    }

    /**
     * Gets the 'phpspec.reporter' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.reporter.class% instance.
     */
    protected function getPhpspec_ReporterService()
    {
        $class = $this->getParameter('phpspec.reporter.class');
        return $this->services['phpspec.reporter'] = new $class();
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
        $instance->setPrintsDetailedProgressReport($this->getParameter('prints_detailed_progress_report'));
        $instance->setStopsOnFailure($this->getParameter('stops_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargets($this->get('test_targets'));
        $instance->setUsesNotification($this->getParameter('uses_notification'));
        $instance->setCliRunner($this->get('phpspec.cli_runner'));
        $instance->setDocumentationFormatterFactory($this->get('phpspec.documentation_formatter_factory'));
        $instance->setJUnitXMLFormatterFactory($this->get('phpspec.junit_xml_formatter_factory'));
        $instance->setNotificationFormatterFactory($this->get('phpspec.notification_formatter_factory'));
        $instance->setProgressFormatterFactory($this->get('phpspec.progress_formatter_factory'));
        $instance->setReporter($this->get('phpspec.reporter'));

        return $instance;
    }

    /**
     * Gets the 'phpspec.spec_loader_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpspec.spec_loader_factory.class% instance.
     */
    protected function getPhpspec_SpecLoaderFactoryService()
    {
        $class = $this->getParameter('phpspec.spec_loader_factory.class');
        return $this->services['phpspec.spec_loader_factory'] = new $class();
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
        $instance->setMonitoringDirectories($this->getParameter('monitoring_directories'));
        $instance->setNotifierFactory($this->get('notifier_factory'));
        $instance->setOS($this->get('os'));
        $instance->setRunnerFactory($this->get('runner_factory'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargets($this->get('test_targets'));
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
        $this->services['phpunit.collector'] = $instance = new $class($this->get('test_targets'));

        $instance->setCollectingTypeFactory($this->get('collecting_type_factory'));
        $instance->setPHPUnitXMLConfiguration($this->get('phpunit.phpunit_xml_configuration'));

        return $instance;
    }

    /**
     * Gets the 'phpunit.junit_xml_printer' service.
     *
     * @return Object A %phpunit.junit_xml_printer.class% instance.
     */
    protected function getPhpunit_JunitXmlPrinterService()
    {
        $class = $this->getParameter('phpunit.junit_xml_printer.class');
        $instance = new $class();

        $instance->setTestTargets($this->get('test_targets'));

        return $instance;
    }

    /**
     * Gets the 'phpunit.junit_xml_printer_factory' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %phpunit.junit_xml_printer_factory.class% instance.
     */
    protected function getPhpunit_JunitXmlPrinterFactoryService()
    {
        $this->services['phpunit.junit_xml_printer_factory'] = $instance = $this->get('component_aware_factory_factory')->create('phpunit.junit_xml_printer', $this->getParameter('phpunit.junit_xml_printer_factory.class'));

        $instance->setJUnitXMLWriterFactory($this->get('junit_xml_writer_factory'));

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
        $instance->setPrintsDetailedProgressReport($this->getParameter('prints_detailed_progress_report'));
        $instance->setStopsOnFailure($this->getParameter('stops_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargets($this->get('test_targets'));
        $instance->setUsesNotification($this->getParameter('uses_notification'));
        $instance->setPHPUnitXMLConfiguration($this->get('phpunit.phpunit_xml_configuration'));
        $instance->setJUnitXMLPrinterFactory($this->get('phpunit.junit_xml_printer_factory'));

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
        $instance->setMonitoringDirectories($this->getParameter('monitoring_directories'));
        $instance->setNotifierFactory($this->get('notifier_factory'));
        $instance->setOS($this->get('os'));
        $instance->setRunnerFactory($this->get('runner_factory'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargets($this->get('test_targets'));

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
        $this->services['simpletest.collector'] = $instance = new $class($this->get('test_targets'));

        $instance->setCollectingTypeFactory($this->get('collecting_type_factory'));

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
        $this->services['simpletest.junit_xml_reporter_factory'] = $instance = $this->get('component_aware_factory_factory')->create('junit_xml_reporter', $this->getParameter('simpletest.junit_xml_reporter_factory.class'));

        $instance->setJUnitXMLWriterFactory($this->get('junit_xml_writer_factory'));

        return $instance;
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
        $instance->setPrintsDetailedProgressReport($this->getParameter('prints_detailed_progress_report'));
        $instance->setStopsOnFailure($this->getParameter('stops_on_failure'));
        $instance->setTerminal($this->get('terminal'));
        $instance->setTestTargets($this->get('test_targets'));
        $instance->setUsesNotification($this->getParameter('uses_notification'));
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
        $instance->setEnablesAutotest($this->getParameter('enables_autotest'));
        $instance->setTestRunFactory($this->get('test_run_factory'));

        return $instance;
    }

    /**
     * Gets the 'test_targets' service.
     *
     * This service is shared.
     * This method always returns the same instance of the service.
     *
     * @return Object A %test_targets.class% instance.
     */
    protected function getTestTargetsService()
    {
        $class = $this->getParameter('test_targets.class');
        $this->services['test_targets'] = $instance = new $class();

        $instance->setClasses($this->getParameter('test_classes'));
        $instance->setFilePattern($this->getParameter('test_file_pattern'));
        $instance->setMethods($this->getParameter('test_methods'));
        $instance->setRecursivelyScans($this->getParameter('recursively_scans'));
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
            'junit_xml_dom_writer.class' => 'Stagehand\\TestRunner\\JUnitXMLWriter\\JUnitXMLDOMWriter',
            'junit_xml_dom_writer_factory.class' => 'Stagehand\\TestRunner\\JUnitXMLWriter\\JUnitXMLDOMWriter',
            'junit_xml_stream_writer.class' => 'Stagehand\\TestRunner\\JUnitXMLWriter\\JUnitXMLStreamWriter',
            'junit_xml_stream_writer_factory.class' => 'Stagehand\\TestRunner\\JUnitXMLWriter\\JUnitXMLStreamWriter',
            'junit_xml_writer_factory.class' => 'Stagehand\\TestRunner\\JUnitXMLWriter\\JUnitXMLWriterFactory',
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
            'test_targets.class' => 'Stagehand\\TestRunner\\Core\\TestTargets',
            'recursively_scans' => false,
            'enables_autotest' => false,
            'monitoring_directories' => array(

            ),
            'uses_notification' => false,
            'test_methods' => array(

            ),
            'test_classes' => array(

            ),
            'junit_xml_file' => NULL,
            'logs_results_in_junit_xml_in_realtime' => false,
            'stops_on_failure' => false,
            'test_file_pattern' => NULL,
            'test_resources' => array(

            ),
            'prints_detailed_progress_report' => false,
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
            'phpspec.cli_runner.class' => 'PHPSpec\\Runner\\Cli\\Runner',
            'phpspec.collector.class' => 'Stagehand\\TestRunner\\Collector\\PHPSpecCollector',
            'phpspec.documentation_formatter.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\Formatter\\DocumentationFormatter',
            'phpspec.documentation_formatter_factory.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\Formatter\\DocumentationFormatterFactory',
            'phpspec.example_factory.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\ExampleFactory',
            'phpspec.example_runner.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\ExampleRunner',
            'phpspec.junit_xml_formatter.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\Formatter\\JUnitXMLFormatter',
            'phpspec.junit_xml_formatter_factory.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\Formatter\\JUnitXMLFormatterFactory',
            'phpspec.notification_formatter.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\Formatter\\NotificationFormatter',
            'phpspec.notification_formatter_factory.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\Formatter\\NotificationFormatterFactory',
            'phpspec.preparer.class' => 'Stagehand\\TestRunner\\Preparer\\PHPSpecPreparer',
            'phpspec.progress_formatter.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\Formatter\\ProgressFormatter',
            'phpspec.progress_formatter_factory.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\Formatter\\ProgressFormatterFactory',
            'phpspec.reporter.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\Reporter',
            'phpspec.runner.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner',
            'phpspec.spec_loader_factory.class' => 'Stagehand\\TestRunner\\Runner\\PHPSpecRunner\\SpecLoaderFactory',
            'phpunit.autotest.class' => 'Stagehand\\TestRunner\\Process\\Autotest\\PHPUnitAutotest',
            'phpunit.collector.class' => 'Stagehand\\TestRunner\\Collector\\PHPUnitCollector',
            'phpunit.junit_xml_printer.class' => 'Stagehand\\TestRunner\\Runner\\PHPUnitRunner\\Printer\\JUnitXMLPrinter',
            'phpunit.junit_xml_printer_factory.class' => 'Stagehand\\TestRunner\\Runner\\PHPUnitRunner\\Printer\\JUnitXMLPrinterFactory',
            'phpunit.phpunit_xml_configuration.class' => 'Stagehand\\TestRunner\\Core\\PHPUnitXMLConfiguration',
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
