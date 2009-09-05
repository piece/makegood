package com.piece_framework.stagehand_testrunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;


public class StagehandTestRunnerLaunchConfigurationDelegate extends
        LaunchConfigurationDelegate {

    @Override
    public void launch(ILaunchConfiguration configuration,
                         String mode,
                         ILaunch launch,
                         IProgressMonitor monitor
                         ) throws CoreException {
        System.out.println(mode);
        System.out.println(launch.getDebugTargets());
    }

}
