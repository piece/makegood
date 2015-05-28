/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2013, 2015 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.launch;

import java.io.File;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.internal.core.LaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.php.debug.core.debugger.parameters.IDebugParametersKeys;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.php.internal.debug.core.debugger.IDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.launching.PHPLaunchDelegateProxy;
import org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames;
import org.eclipse.php.internal.debug.core.preferences.PHPDebuggersRegistry;
import org.eclipse.php.internal.debug.core.preferences.PHPProjectPreferences;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;

import com.piece_framework.makegood.stagehandtestrunner.StagehandTestRunner;

@SuppressWarnings("restriction")
public class MakeGoodLaunchConfigurationDelegate extends PHPLaunchDelegateProxy {
    private static final String MAKEGOOD_JUNIT_XML_FILE = "MAKEGOOD_JUNIT_XML_FILE"; //$NON-NLS-1$
    private String delegateClass;

    @Override
    public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        if (!configuration.exists()) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "The configuration for the launch is not found.")); //$NON-NLS-1$
            cancelLaunch(monitor);
            return false;
        }

        boolean result;
        try {
            result = super.finalLaunchCheck(configuration, mode, monitor);
        } catch (DebugException e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            cancelLaunch(monitor);
            throw e;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        }
        if (!result) {
            cancelLaunch(monitor);
        }
        return result;
    }

    @Override
    public ILaunch getLaunch(ILaunchConfiguration originalConfiguration, String mode) throws CoreException {
        try {
            ILaunchConfiguration configuration = createConfiguration(originalConfiguration);
            delegateClass = configuration.getAttribute(PHPDebugCorePreferenceNames.CONFIGURATION_DELEGATE_CLASS, ""); //$NON-NLS-1$
            MakeGoodLaunch launch = new MakeGoodLaunch(configuration, mode, null);
            TestLifecycle.getInstance().setLaunch(launch);
            setLaunchPerspective(configuration, mode);
            return launch;
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            cancelLaunch();
            throw e;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch();
            throw new CoreException(status);
        }
    }

    /**
     * @since 1.7.0
     */
    private void setLaunchPerspective(ILaunchConfiguration configuration, String mode) throws CoreException {
        HashSet<String> modes = new HashSet<String>();
        modes.add(mode);

        ILaunchConfigurationType launchConfigurationType =
            DebugPlugin.getDefault()
                .getLaunchManager()
                .getLaunchConfigurationType(IPHPDebugConstants.PHPEXELaunchType);
        if (launchConfigurationType == null) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "The launch configuration type for the ID [ " + IPHPDebugConstants.PHPEXELaunchType + " ] is not found.")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        ILaunchDelegate[] launchDelegates = launchConfigurationType.getDelegates(modes);
        if (launchDelegates.length == 0) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "The delegates capable of launching in the specified modes for the launch configuration type ID [ " + IPHPDebugConstants.PHPEXELaunchType + " ] are not found.")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        DebugUITools.setLaunchPerspective(
            configuration.getType(),
            mode,
            DebugUITools.getLaunchPerspective(launchConfigurationType, launchDelegates[0], modes)
        );
    }

    @Override
    public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        if (!configuration.exists()) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "The configuration for the launch is not found.")); //$NON-NLS-1$
            cancelLaunch(monitor);
            return false;
        }

        boolean result;
        try {
            result = super.preLaunchCheck(configuration, mode, monitor);
        } catch (DebugException e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            cancelLaunch(monitor);
            throw e;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        }
        if (!result) {
            cancelLaunch(monitor);
        }
        return result;
    }

    @Override
    public void launch(
        ILaunchConfiguration originalConfiguration,
        String mode,
        ILaunch launch,
        IProgressMonitor monitor) throws CoreException {
        ILaunchConfiguration configuration = launch.getLaunchConfiguration();
        if (configuration == null) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "No configuration was launched.")); //$NON-NLS-1$
            cancelLaunch(monitor);
            return;
        }

        if (!configuration.exists()) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "The configuration for the launch is not found.")); //$NON-NLS-1$
            cancelLaunch(monitor);
            return;
        }

        try {
            JUnitXMLRegistry.create();
        } catch (SecurityException e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        }

        try {
            super.launch(configuration, mode, launch, monitor);
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            cancelLaunch(monitor);
            throw e;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        }
    }

    @Override
    /**
     * @since 1.2.0
     */
    protected ILaunchConfigurationDelegate2 getConfigurationDelegate(ILaunchConfiguration configuration) throws CoreException {
        if (launchConfigurationDelegate == null) {
            try {
                if (delegateClass.length() == 0) {
                    throw new IllegalArgumentException();
                }

                launchConfigurationDelegate = (ILaunchConfigurationDelegate2) Class
                .forName(delegateClass).newInstance();
            } catch (Throwable t) {
                throw new CoreException(new Status(IStatus.ERROR,
                        PHPDebugPlugin.ID, 0,
                        "Launch configuration delegate loading error.", t));
            }
        }
        return launchConfigurationDelegate;
    }

    private ILaunchConfiguration createConfiguration(
        ILaunchConfiguration configuration) throws CoreException, MethodNotFoundException, ResourceNotFoundException {
        String configurationName =
            "MakeGood" + Long.toString(System.currentTimeMillis()); //$NON-NLS-1$
        String junitXMLFile =
            JUnitXMLRegistry.getRegistry().getAbsolutePath().toString() +
            String.valueOf(File.separatorChar) +
            configurationName +
            ".xml"; //$NON-NLS-1$

        ILaunchConfigurationWorkingCopy workingCopy = new LaunchConfigurationWorkingCopy((LaunchConfiguration) configuration) {
            @Override
            public synchronized ILaunchConfiguration doSave() throws CoreException {
                return null;
            }
        };

        String mainScript = TestLifecycle.getInstance().getTestTargets().getMainScript();
        if (mainScript == null) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "The main script is not found.")); //$NON-NLS-1$
        }

        workingCopy.setAttribute(IPHPDebugConstants.ATTR_FILE, mainScript);

        workingCopy.setAttribute(
            IPHPDebugConstants.ATTR_FILE_FULL_PATH, getCommandPath()
        );
        workingCopy.setAttribute(MAKEGOOD_JUNIT_XML_FILE, junitXMLFile);
        workingCopy.setAttribute(
            IDebugParametersKeys.EXE_CONFIG_PROGRAM_ARGUMENTS,
            new CommandLineBuilder(junitXMLFile).build()
        );

        IProject project = TestLifecycle.getInstance().getTestTargets().getProject();
        if (project != null && project.exists()) {
            workingCopy.setAttribute(IPHPDebugConstants.PHP_Project, project.getName());
            rewriteBasicConfigurationAttributes(workingCopy, project);
        }

        return workingCopy;
    }

    public static String getCommandPath() throws CoreException {
        return StagehandTestRunner.getCommandPath();
    }

    public static String getJUnitXMLFile(ILaunch launch) throws CoreException {
        return launch.getLaunchConfiguration().getAttribute(MAKEGOOD_JUNIT_XML_FILE, (String) null);
    }

    private void rewriteBasicConfigurationAttributes(ILaunchConfigurationWorkingCopy workingCopy, IProject project) {
        PHPexeItem phpexeItem = new PHPexeItemRepository().findByProject(project);
        if (phpexeItem == null) return;

        workingCopy.setAttribute(PHPDebugCorePreferenceNames.PHP_DEBUGGER_ID, phpexeItem.getDebuggerID());
        workingCopy.setAttribute(PHPDebugCorePreferenceNames.CONFIGURATION_DELEGATE_CLASS, getDebuggerConfiguration(phpexeItem.getDebuggerID()).getScriptLaunchDelegateClass());
        workingCopy.setAttribute(IPHPDebugConstants.ATTR_EXECUTABLE_LOCATION, phpexeItem.getExecutable().getAbsolutePath().toString());
        workingCopy.setAttribute(IPHPDebugConstants.ATTR_INI_LOCATION, phpexeItem.getINILocation() != null ? phpexeItem.getINILocation().toString() : null);
        workingCopy.setAttribute(IPHPDebugConstants.RUN_WITH_DEBUG_INFO, PHPDebugPlugin.getDebugInfoOption());
        workingCopy.setAttribute(IDebugParametersKeys.FIRST_LINE_BREAKPOINT, PHPProjectPreferences.getStopAtFirstLine(project));
    }

    private void cancelLaunch() {
        TestLifecycle.destroy();
    }

    private void cancelLaunch(IProgressMonitor monitor) {
        monitor.setCanceled(true);
        cancelLaunch();
    }

    /**
     * @since 3.2.0
     */
    private static IDebuggerConfiguration getDebuggerConfiguration(String debuggerId) {
        for (IDebuggerConfiguration debuggerConfiguration: PHPDebuggersRegistry.getDebuggersConfigurations()) {
            if (debuggerId.equals(debuggerConfiguration.getDebuggerId())) {
                return debuggerConfiguration;
            }
        }

        return null;
    }
}
