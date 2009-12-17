package com.piece_framework.makegood.launch;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

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
        String log = MakeGoodLauncherRegistry.getRegistry().getAbsolutePath().toString() +
                     String.valueOf(File.separatorChar) +
                     configurationName +
                     ".xml"; //$NON-NLS-1$
        MakeGoodLaunchParameter parameter = MakeGoodLaunchParameter.get();
        MakeGoodLauncher launcher = getLauncher();

        ILaunchConfigurationWorkingCopy workingCopy =
            new MakeGoodLaunchConfigurationWorkingCopy(configuration.copy(configurationName));
        workingCopy.setAttribute("ATTR_FILE", parameter.getMainScript()); //$NON-NLS-1$
        workingCopy.setAttribute("ATTR_FILE_FULL_PATH", launcher.getScript()); //$NON-NLS-1$
        workingCopy.setAttribute("LOG_JUNIT", log); //$NON-NLS-1$
        workingCopy.setAttribute("exeDebugArguments", parameter.generateParameter(log)); //$NON-NLS-1$

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

    private MakeGoodLauncher getLauncher() throws CoreException {
        MakeGoodLauncher launcher = null;
        try {
            MakeGoodLauncherRegistry registry = new MakeGoodLauncherRegistry();
            launcher = registry.getLauncher(MakeGoodLaunchParameter.get().getTestingFramework());
        } catch (FileNotFoundException e) {
            throw new CoreException(new Status(IStatus.ERROR,
                                               Activator.PLUGIN_ID,
                                               0,
                                               e.getMessage(),
                                               e
                                               ));
        }
        return launcher;
    }
}
