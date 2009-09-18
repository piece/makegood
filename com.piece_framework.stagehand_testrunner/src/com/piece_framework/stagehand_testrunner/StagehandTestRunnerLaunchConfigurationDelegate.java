package com.piece_framework.stagehand_testrunner;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.php.internal.debug.core.model.IPHPDebugTarget;


public class StagehandTestRunnerLaunchConfigurationDelegate extends
        LaunchConfigurationDelegate {
    @Override
    public void launch(ILaunchConfiguration configuration,
                         String mode,
                         ILaunch launch,
                         IProgressMonitor monitor
                         ) throws CoreException {
        String testFile = configuration.getAttribute("ATTR_FILE", (String) null);

        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IResource testResource = workspaceRoot.findMember(testFile);
        IProject project = testResource.getProject();
        IResource testRunnerResource = project.findMember("/bin/testrunner");
        IResource prepareResource = project.findMember("/tests/prepare.php");

        ILaunchConfigurationWorkingCopy workingCopy = configuration.copy(Long.toString(System.currentTimeMillis()));
        workingCopy.setAttribute("ATTR_FILE",
                                 testRunnerResource.getFullPath().toString()
                                 );
        workingCopy.setAttribute("ATTR_FILE_FULL_PATH",
                                 testRunnerResource.getLocation().toString()
                                 );
        workingCopy.setAttribute("exeDebugArguments",
                                 "-p " + prepareResource.getLocation().toString() +
                                     " " + testResource.getLocation().toString()
                                 );

        IDebugEventSetListener listener = new IDebugEventSetListener() {
            @Override
            public void handleDebugEvents(DebugEvent[] events) {
                for (DebugEvent event: events) {
                    if (event.getKind() == DebugEvent.TERMINATE
                        && event.getSource() instanceof IPHPDebugTarget
                        ) {
                        IPHPDebugTarget debugTarget = (IPHPDebugTarget) event.getSource();
                        System.out.println(debugTarget.getOutputBuffer().toString());
                    }
                }
            }
        };
        DebugPlugin.getDefault().addDebugEventListener(listener);

        ILaunch launchForWorkingCopy = new Launch(workingCopy,
                                                  launch.getLaunchMode(),
                                                  launch.getSourceLocator()
                                                  );
        launchForWorkingCopy.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT,
                                          launch.getAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT)
                                          );
        launchForWorkingCopy.setAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING,
                                          launch.getAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING)
                                          );

        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        manager.removeLaunch(launch);
        manager.addLaunch(launchForWorkingCopy);

        Set modes = new HashSet();
        modes.add(mode);
        ILaunchConfigurationType configurationType = manager.getLaunchConfigurationType("org.eclipse.php.debug.core.launching.PHPExeLaunchConfigurationType");
        ILaunchDelegate delegate = configurationType.getDelegates(modes)[0];

        delegate.getDelegate().launch(workingCopy, mode, launchForWorkingCopy, monitor);
    }
}
