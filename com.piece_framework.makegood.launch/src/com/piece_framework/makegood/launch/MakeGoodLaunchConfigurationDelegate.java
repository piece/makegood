/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.php.debug.core.debugger.parameters.IDebugParametersKeys;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.launching.PHPLaunch;
import org.eclipse.php.internal.debug.core.launching.PHPLaunchDelegateProxy;
import org.eclipse.php.internal.debug.core.model.IPHPDebugTarget;
import org.eclipse.php.internal.debug.core.sourcelookup.PHPSourceLookupDirector;
import org.eclipse.php.internal.debug.core.sourcelookup.PHPSourcePathComputerDelegate;
import org.eclipse.php.internal.debug.ui.PHPDebugPerspectiveFactory;

import com.piece_framework.makegood.stagehand_testrunner.StagehandTestRunner;

public class MakeGoodLaunchConfigurationDelegate extends PHPLaunchDelegateProxy {
    private static final String MAKEGOOD_JUNIT_XML_FILE = "MAKEGOOD_JUNIT_XML_FILE"; //$NON-NLS-1$
    private static final String MAKEGOOD_LAUNCH_MARKER = "MAKEGOOD_LAUNCH_MARKER"; //$NON-NLS-1$
    private Boolean isLocked = false;

    @Override
    public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        return false;
    }

    @Override
    public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        boolean result;
        try {
            result = super.finalLaunchCheck(configuration, mode, monitor);
        } catch (CoreException e) {
            configuration.delete();
            throw e;
        }

        if (!result) {
            configuration.delete();
        }

        return result;
    }

	@Override
    public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
        return new PHPLaunch(configuration, mode, null);
    }

    @Override
    public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        boolean result;
        try {
            result = super.preLaunchCheck(configuration, mode, monitor);
        } catch (CoreException e) {
            configuration.delete();
            throw e;
        }

        if (!result) {
            configuration.delete();
        }

        return result;
    }

    @Override
    public void launch(
        ILaunchConfiguration originalConfiguration,
        String mode,
        ILaunch originalLaunch,
        IProgressMonitor monitor
    ) throws CoreException {
        synchronized (isLocked) {
            if (isLocked) {
                originalConfiguration.delete();
                monitor.setCanceled(true);
                monitor.done();
                return;
            }
        }

        isLocked = true;

        try {
            JUnitXMLRegistry.create();
        } catch (SecurityException e) {
            isLocked = false;
            originalConfiguration.delete();
            monitor.setCanceled(true);
            monitor.done();
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
        }

        ILaunchConfiguration configuration = null;
        try {
            configuration = createConfiguration(originalConfiguration);
        } catch (CoreException e) {
            isLocked = false;
            originalConfiguration.delete();
            monitor.setCanceled(true);
            monitor.done();
            throw e;
        }

        ILaunch launch = null;
        try {
            launch = createLaunch(originalLaunch, configuration);
        } catch (CoreException e) {
            isLocked = false;
            monitor.setCanceled(true);
            monitor.done();
            throw e;
        }

        if (ILaunchManager.DEBUG_MODE.equals(mode)) {
            try {
                switchToPHPDebugPerspective(configuration);
            } catch (CoreException e) {
                isLocked = false;
                monitor.setCanceled(true);
                monitor.done();
                throw e;
            }
        }

        try {
            super.launch(configuration, mode, launch, monitor);
        } catch (CoreException e) {
            isLocked = false;
            monitor.setCanceled(true);
            monitor.done();
            throw e;
        }

        isLocked = false;
    }

    public static boolean hasActiveMakeGoodLaunches() {
        ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
        for (int i = 0; i < launches.length; i++) {
            if (launches[i].isTerminated()) continue;
            if (isMakeGoodLaunch(launches[i])) {
                return true;
            }
        }

        return false;
    }

    public static boolean isMakeGoodLaunch(ILaunch launch) {
        return Boolean.TRUE.toString().equals(launch.getAttribute(MAKEGOOD_LAUNCH_MARKER));
    }

    public static ILaunch getLaunch(Object eventSource) {
        ILaunch launch = null;
        if (eventSource instanceof IPHPDebugTarget) {
            launch = ((IPHPDebugTarget) eventSource).getLaunch();
        } else if (eventSource instanceof IProcess) {
            launch = ((IProcess) eventSource).getLaunch();
        }

        return launch;
    }

    private ILaunchConfiguration createConfiguration(
        ILaunchConfiguration configuration) throws CoreException {
        String configurationName =
            "MakeGood" + Long.toString(System.currentTimeMillis()); //$NON-NLS-1$
        String junitXMLFile =
            JUnitXMLRegistry.getRegistry().getAbsolutePath().toString() +
            String.valueOf(File.separatorChar) +
            configurationName +
            ".xml"; //$NON-NLS-1$
        LaunchTarget generator = LaunchTarget.getInstance();

        ILaunchConfigurationWorkingCopy workingCopy =
            new MakeGoodLaunchConfigurationWorkingCopy(
                configuration.copy(configurationName)
            );
        workingCopy.setAttribute(
            IPHPDebugConstants.ATTR_FILE, generator.getMainScript()
        );
        workingCopy.setAttribute(
            IPHPDebugConstants.ATTR_FILE_FULL_PATH, getCommandPath()
        );
        workingCopy.setAttribute(MAKEGOOD_JUNIT_XML_FILE, junitXMLFile);
        workingCopy.setAttribute(
            IDebugParametersKeys.EXE_CONFIG_PROGRAM_ARGUMENTS,
            generator.getProgramArguments(junitXMLFile)
        );

        IProject project = generator.getMainScriptResource().getProject();
        if (project != null && project.exists()) {
            workingCopy.setAttribute(IPHPDebugConstants.PHP_Project, project.getName());
        }

        configuration.delete();

        return workingCopy;
    }

    private ILaunch createLaunch(
        ILaunch originalLaunch,
        ILaunchConfiguration configuration) throws CoreException {
        ILaunch launch =
            new PHPLaunch(
                configuration,
                originalLaunch.getLaunchMode(),
                createSourceLocator(configuration, originalLaunch.getLaunchMode())
            );
        launch.setAttribute(
            DebugPlugin.ATTR_CAPTURE_OUTPUT,
            originalLaunch.getAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT)
        );
        launch.setAttribute(
            DebugPlugin.ATTR_CONSOLE_ENCODING,
            originalLaunch.getAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING)
        );
        launch.setAttribute(MAKEGOOD_LAUNCH_MARKER, Boolean.TRUE.toString());

        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        manager.removeLaunch(originalLaunch);
        manager.addLaunch(launch);

        return launch;
    }

    public static String getCommandPath() throws CoreException {
        return StagehandTestRunner.getCommandPath(
                   LaunchTarget.getInstance().getTestingFramework().name()
               );
    }

    public static String getJUnitXMLFile(ILaunch launch) throws CoreException {
        return launch.getLaunchConfiguration().getAttribute(MAKEGOOD_JUNIT_XML_FILE, (String) null);
    }

    private void switchToPHPDebugPerspective(ILaunchConfiguration configuration)
        throws CoreException {
        DebugUITools.setLaunchPerspective(
            configuration.getType(),
            ILaunchManager.DEBUG_MODE,
            PHPDebugPerspectiveFactory.PERSPECTIVE_ID
        );
    }

    private ISourceLocator createSourceLocator(
        ILaunchConfiguration configuration,
        String launchMode) throws CoreException {
        PHPSourceLookupDirector sourceLocator = null;
        if (ILaunchManager.DEBUG_MODE.equals(launchMode)) {
            sourceLocator = new PHPSourceLookupDirector();
            sourceLocator.initializeParticipants();
            sourceLocator.setSourceContainers(
                new PHPSourcePathComputerDelegate().computeSourceContainers(
                    configuration, null
                )
            );
        }

        return sourceLocator;
    }
}
