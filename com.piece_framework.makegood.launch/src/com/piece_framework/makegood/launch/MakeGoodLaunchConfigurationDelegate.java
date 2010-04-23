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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import com.piece_framework.makegood.stagehand_testrunner.StagehandTestRunner;

public class MakeGoodLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {
    public void launch(ILaunchConfiguration configuration,
                       String mode,
                       ILaunch launch,
                       IProgressMonitor monitor
                       ) throws CoreException {
        ILaunchConfiguration stagehandTestRunnerLaunchConfiguration =
            createStagehandTestRunnerLaunchConfiguration(launch, configuration);

        ILaunch stagehandTestRunnerLaunch = replaceLaunch(launch, stagehandTestRunnerLaunchConfiguration);

        Set modes = new HashSet();
        modes.add(mode);
        ILaunchConfigurationType configurationType =
            DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.eclipse.php.debug.core.launching.PHPExeLaunchConfigurationType"); //$NON-NLS-1$
        ILaunchDelegate delegate = configurationType.getDelegates(modes)[0];

        JUnitXMLRegistry.create();

        delegate.getDelegate().launch(stagehandTestRunnerLaunchConfiguration,
                                      mode,
                                      stagehandTestRunnerLaunch,
                                      monitor
                                      );
    }

    private ILaunchConfiguration createStagehandTestRunnerLaunchConfiguration(ILaunch launch,
                                                                              ILaunchConfiguration configuration
                                                                              ) throws CoreException {
        String configurationName = "MakeGood" + Long.toString(System.currentTimeMillis()); //$NON-NLS-1$
        String log = JUnitXMLRegistry.getRegistry().getAbsolutePath().toString() +
                     String.valueOf(File.separatorChar) +
                     configurationName +
                     ".xml"; //$NON-NLS-1$
        CommandLineGenerator parameter = CommandLineGenerator.getInstance();

        ILaunchConfigurationWorkingCopy workingCopy =
            new MakeGoodLaunchConfigurationWorkingCopy(configuration.copy(configurationName));
        workingCopy.setAttribute("ATTR_FILE", parameter.getMainScript()); //$NON-NLS-1$
        workingCopy.setAttribute("ATTR_FILE_FULL_PATH", getCommandPath()); //$NON-NLS-1$
        workingCopy.setAttribute("LOG_JUNIT", log); //$NON-NLS-1$
        workingCopy.setAttribute("exeDebugArguments", parameter.generate(log)); //$NON-NLS-1$

        configuration.delete();

        return workingCopy;
    }

    private ILaunch replaceLaunch(ILaunch launch, ILaunchConfiguration configuration) {
        ILaunch newLaunch = new Launch(configuration,
                                       launch.getLaunchMode(),
                                       launch.getSourceLocator()
                                       );
        newLaunch.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT,
                               launch.getAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT)
                               );
        newLaunch.setAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING,
                               launch.getAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING)
                               );

        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        manager.removeLaunch(launch);
        manager.addLaunch(newLaunch);

        return newLaunch;
    }

    private String getCommandPath() throws CoreException {
        return StagehandTestRunner.getCommandPath(
                   CommandLineGenerator.getInstance().getTestingFramework().name()
               );
    }
}
