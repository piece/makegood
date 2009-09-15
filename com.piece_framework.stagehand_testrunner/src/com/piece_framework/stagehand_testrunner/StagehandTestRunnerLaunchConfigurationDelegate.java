package com.piece_framework.stagehand_testrunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.php.internal.debug.core.launching.PHPLaunchDelegateProxy;


public class StagehandTestRunnerLaunchConfigurationDelegate extends
        PHPLaunchDelegateProxy {

    @Override
    protected ILaunchConfigurationDelegate2 getConfigurationDelegate(
            ILaunchConfiguration configuration) throws CoreException {
        ILaunchConfigurationDelegate2 delegate = super.getConfigurationDelegate(configuration);
        System.out.println(delegate);
        return delegate;
    }
}
