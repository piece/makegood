package com.piece_framework.makegood.launch;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
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
        File tempDir = new File(System.getProperty("java.io.tmpdir"), Activator.PLUGIN_ID);
        if (!tempDir.exists()) {
            tempDir.mkdir();

            URL scriptsURL = Platform.getBundle(Activator.PLUGIN_ID).getEntry("launchers");
            File scriptsDirectory = null;
            try {
                scriptsDirectory = new File(FileLocator.toFileURL(scriptsURL).getPath());
            } catch (IOException e) {
                // TODO: 
                e.printStackTrace();
            }
            for (File launchScript : scriptsDirectory.listFiles()) {
                File newLaunchScript = new File(tempDir.getAbsolutePath() + "/" + launchScript.getName());
                FileOutputStream output = null;
                FileInputStream input = null;

                try {
                    newLaunchScript.createNewFile();
                    output = new FileOutputStream(newLaunchScript);
                    input = new FileInputStream(launchScript);
                    
                    int data = 0;
                    while ((data = input.read()) > 0) {
                        output.write(data);
                    }
                } catch (IOException e) {
                    // TODO: 
                    e.printStackTrace();
                } finally {
                    try {
                        if (output != null) {
                            output.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException e) {}
                }
            }
        }
        String launchScriptFile = new File(tempDir.getAbsolutePath() + "/testrunner").getAbsolutePath();

        String testFile = configuration.getAttribute("ATTR_FILE", (String) null);

        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IResource testResource = workspaceRoot.findMember(testFile);
        IProject project = testResource.getProject();
        IResource prepareResource = project.findMember("/tests/prepare.php");

        ILaunchConfigurationWorkingCopy workingCopy = configuration.copy(Long.toString(System.currentTimeMillis()));

        workingCopy.setAttribute("ATTR_FILE",
                                 testFile
                                 );
        workingCopy.setAttribute("ATTR_FILE_FULL_PATH",
                                 launchScriptFile
                                 );
        workingCopy.setAttribute("exeDebugArguments",
                                 "-p " + prepareResource.getLocation().toString() +
                                     " " + testResource.getLocation().toString()
                                 );

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
