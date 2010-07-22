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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.internal.core.LaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.php.debug.core.debugger.parameters.IDebugParametersKeys;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.launching.PHPLaunch;
import org.eclipse.php.internal.debug.core.launching.PHPLaunchDelegateProxy;
import org.eclipse.php.internal.debug.core.model.IPHPDebugTarget;
import org.eclipse.php.internal.debug.core.sourcelookup.PHPSourcePathComputerDelegate;
import org.eclipse.php.internal.debug.ui.PHPDebugPerspectiveFactory;

import com.piece_framework.makegood.stagehand_testrunner.StagehandTestRunner;

public class MakeGoodLaunchConfigurationDelegate extends PHPLaunchDelegateProxy {
    private static final String MAKEGOOD_JUNIT_XML_FILE = "MAKEGOOD_JUNIT_XML_FILE"; //$NON-NLS-1$
    private static final String MAKEGOOD_LAUNCH_MARKER = "MAKEGOOD_LAUNCH_MARKER"; //$NON-NLS-1$
    private final Object launchLock = new Object();
    private ILaunchConfiguration currentConfiguration;
    private boolean preLaunchCheckCalled = false;

    @Override
    public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        synchronized (launchLock) {
            if (currentConfiguration != null) {
                return false;
            }

            if (!configuration.exists()) {
                return false;
            }
        }

        boolean result;
        try {
            result = super.finalLaunchCheck(configuration, mode, monitor);
        } catch (DebugException e) {
            configuration.delete();
            synchronized (launchLock) {
                preLaunchCheckCalled = false;
            }
            return false;
        } catch (CoreException e) {
            configuration.delete();
            synchronized (launchLock) {
                preLaunchCheckCalled = false;
            }
            throw e;
        }
        if (!result) {
            configuration.delete();
            synchronized (launchLock) {
                preLaunchCheckCalled = false;
            }
        }
        return result;
    }

    @Override
    public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
        return new PHPLaunch(configuration, mode, null);
    }

    @Override
    public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        synchronized (launchLock) {
            if (currentConfiguration != null) {
                monitor.setCanceled(true);
                return false;
            }

            if (!configuration.exists()) {
                monitor.setCanceled(true);
                return false;
            }
        }

        boolean result;
        try {
            synchronized (launchLock) {
                if (preLaunchCheckCalled) {
                    monitor.setCanceled(true);
                    return false;
                }

                result = super.preLaunchCheck(configuration, mode, monitor);
                preLaunchCheckCalled = true;
            }
        } catch (DebugException e) {
            configuration.delete();
            synchronized (launchLock) {
                preLaunchCheckCalled = false;
            }
            monitor.setCanceled(true);
            return false;
        } catch (CoreException e) {
            configuration.delete();
            synchronized (launchLock) {
                preLaunchCheckCalled = false;
            }
            monitor.setCanceled(true);
            throw e;
        }
        if (!result) {
            configuration.delete();
            synchronized (launchLock) {
                preLaunchCheckCalled = false;
            }
            monitor.setCanceled(true);
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
        synchronized (launchLock) {
            if (currentConfiguration != null) {
                monitor.setCanceled(true);
                return;
            }

            if (!originalConfiguration.exists()) {
                monitor.setCanceled(true);
                return;
            }

            currentConfiguration = originalConfiguration;
        }

        try {
            try {
                JUnitXMLRegistry.create();
            } catch (SecurityException e) {
                monitor.setCanceled(true);
                throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            }

            ILaunchConfiguration configuration = null;
            try {
                configuration = createConfiguration(originalConfiguration);
            } catch (CoreException e) {
                monitor.setCanceled(true);
                throw e;
            }

            ILaunch launch = null;
            try {
                launch = createLaunch(originalLaunch, configuration);
            } catch (CoreException e) {
                monitor.setCanceled(true);
                throw e;
            }

            if (ILaunchManager.DEBUG_MODE.equals(mode)) {
                try {
                    switchToPHPDebugPerspective(configuration);
                } catch (CoreException e) {
                    monitor.setCanceled(true);
                    throw e;
                }
            }

            try {
                super.launch(configuration, mode, launch, monitor);
            } catch (CoreException e) {
                monitor.setCanceled(true);
                throw e;
            }
        } finally {
            synchronized (launchLock) {
                preLaunchCheckCalled = false;
                if (currentConfiguration != null) {
                    currentConfiguration.delete();
                    currentConfiguration = null;
                }
            }
        }
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

        ILaunchConfigurationWorkingCopy workingCopy = new LaunchConfigurationWorkingCopy((LaunchConfiguration) configuration) {
            @Override
            public synchronized ILaunchConfiguration doSave() throws CoreException {
                return null;
            }
        };

        LaunchTarget launchTarget = LaunchTarget.getInstance();
        String mainScript = launchTarget.getMainScript();
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
            launchTarget.getProgramArguments(junitXMLFile)
        );

        IResource mainScriptResource = launchTarget.getMainScriptResource();
        IProject project = mainScriptResource.getProject();
        if (mainScriptResource == null) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "The main script resource is not found.")); //$NON-NLS-1$
        }

        if (project != null && project.exists()) {
            workingCopy.setAttribute(IPHPDebugConstants.PHP_Project, project.getName());
        }

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
            sourceLocator.initializeDefaults(configuration);
            sourceLocator.setSourceContainers(
                new PHPSourcePathComputerDelegate().computeSourceContainers(
                    configuration, null
                )
            );
        }

        return sourceLocator;
    }
}
