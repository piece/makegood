package com.piece_framework.makegood.launch;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IType;

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
            DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.eclipse.php.debug.core.launching.PHPExeLaunchConfigurationType");
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
        MakeGoodLaunchParameter parameter = MakeGoodLaunchParameter.get();

        boolean targetIsFolder = parameter.getTarget() instanceof IProjectFragment
                                 || parameter.getTarget() instanceof IScriptFolder
                                 || parameter.getTarget() instanceof IFolder;
        String target = null;
        if (targetIsFolder) {
            target = parameter.getTargetResource().getFullPath().toString();
        } else {
            target = parameter.getScript();
        }

        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IResource targetResource = workspaceRoot.findMember(target);
        IResource prepareResource = workspaceRoot.findMember(parameter.getPreloadScript());

        boolean targetIsClass = parameter.getTarget() instanceof IType;
        String targetClass = null;
        if (targetIsClass) {
            targetClass = ((IType) parameter.getTarget()).getElementName();
        }

        boolean targetIsMethod = parameter.getTarget() instanceof IMethod;
        String targetMethod = null;
        if (targetIsMethod) {
            IMethod method = (IMethod) parameter.getTarget();
            targetMethod = method.getParent().getElementName() + "::" + method.getElementName();
        }

        MakeGoodLauncher launcher = null;
        try {
            MakeGoodLauncherRegistry registry = new MakeGoodLauncherRegistry();
            launcher = registry.getLauncher(TestingFramework.PHPUnit);
        } catch (FileNotFoundException e) {
            throw new CoreException(new Status(IStatus.ERROR,
                                               Activator.PLUGIN_ID,
                                               0,
                                               e.getMessage(),
                                               e
                                               ));
        }

        String configurationName = Long.toString(System.currentTimeMillis());
        ILaunchConfigurationWorkingCopy workingCopy = configuration.copy(configurationName);
        workingCopy.setAttribute("ATTR_FILE",
                                 targetIsFolder ? parameter.getScript()
                                                : target
                                 );
        workingCopy.setAttribute("ATTR_FILE_FULL_PATH",
                                 launcher.getScript()
                                 );
        String logFile = MakeGoodLauncherRegistry.getRegistry().getAbsolutePath().toString() +
                         String.valueOf(File.separatorChar) +
                         configurationName +
                         ".xml";
        workingCopy.setAttribute("LOG_JUNIT", logFile);
        StringBuilder argument = new StringBuilder();
        argument.append("-p " + prepareResource.getLocation().toString());
        argument.append(" --log-junit=" + logFile);
        argument.append(targetIsClass ? " --classes=" + targetClass : "");
        argument.append(targetIsMethod ? " -m " + targetMethod : "");
        argument.append(targetIsFolder ? " -R" : "");
        argument.append(" " + targetResource.getLocation().toString());
        workingCopy.setAttribute("exeDebugArguments",
                                 argument.toString()
                                 );
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
}
