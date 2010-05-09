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
    public static final String JUNIT_XML_FILE = "JUNIT_XML_FILE"; //$NON-NLS-1$
    private static final String MAKEGOOD_LAUNCH_MARKER = "MAKEGOOD_LAUNCH_MARKER"; //$NON-NLS-1$

    @Override
    public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
        return new PHPLaunch(configuration, mode, null);
    }

    @Override
    public void launch(
        ILaunchConfiguration originalConfiguration,
        String mode,
        ILaunch originalLaunch,
        IProgressMonitor monitor
    ) throws CoreException {
        JUnitXMLRegistry.create();
        ILaunchConfiguration configuration =
            createConfiguration(originalConfiguration);
        ILaunch launch = createLaunch(originalLaunch, configuration);
        if (ILaunchManager.DEBUG_MODE.equals(mode)) {
            switchToPHPDebugPerspective(configuration);
        }
        super.launch(configuration, mode, launch, monitor);
    }

    public static boolean hasActiveLaunches() {
        ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
        for (int i = 0; i < launches.length; i++) {
            if (launches[i].isTerminated()) continue;
            if (launches[i].getAttribute(MAKEGOOD_LAUNCH_MARKER) != null) {
                return true;
            }
        }

        return false;
    }

    public static boolean isMakeGoodLaunch(ILaunch launch) {
        return launch.getAttribute(MAKEGOOD_LAUNCH_MARKER) != null;
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
        CommandLineGenerator generator = CommandLineGenerator.getInstance();

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
        workingCopy.setAttribute(JUNIT_XML_FILE, junitXMLFile);
        workingCopy.setAttribute(
            IDebugParametersKeys.EXE_CONFIG_PROGRAM_ARGUMENTS,
            generator.generate(junitXMLFile)
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
        launch.setAttribute(MAKEGOOD_LAUNCH_MARKER, "1"); //$NON-NLS-1$

        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        manager.removeLaunch(originalLaunch);
        manager.addLaunch(launch);

        return launch;
    }

    public static String getCommandPath() throws CoreException {
        return StagehandTestRunner.getCommandPath(
                   CommandLineGenerator.getInstance().getTestingFramework().name()
               );
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
