/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2013 KUBO Atsuhiro <kubo@iteman.jp>,
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.internal.core.LaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.php.debug.core.debugger.parameters.IDebugParametersKeys;
import org.eclipse.php.internal.core.typeinference.PHPClassType;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.php.internal.debug.core.launching.PHPLaunchDelegateProxy;
import org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames;
import org.eclipse.php.internal.debug.core.preferences.PHPDebuggersRegistry;
import org.eclipse.php.internal.debug.core.preferences.PHPProjectPreferences;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;

import com.piece_framework.makegood.core.PHPType;
import com.piece_framework.makegood.core.TestingFramework;
import com.piece_framework.makegood.core.preference.MakeGoodProperty;
import com.piece_framework.makegood.stagehandtestrunner.StagehandTestRunner;

public class MakeGoodLaunchConfigurationDelegate extends PHPLaunchDelegateProxy {
    private static final String MAKEGOOD_JUNIT_XML_FILE = "MAKEGOOD_JUNIT_XML_FILE"; //$NON-NLS-1$
    private String delegateClass;

    @Override
    public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        if (!configuration.exists()) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "The configuration for the launch is not found.")); //$NON-NLS-1$
            cancelLaunch(monitor);
            return false;
        }

        boolean result;
        try {
            result = super.finalLaunchCheck(configuration, mode, monitor);
        } catch (DebugException e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            cancelLaunch(monitor);
            throw e;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        }
        if (!result) {
            cancelLaunch(monitor);
        }
        return result;
    }

    @Override
    public ILaunch getLaunch(ILaunchConfiguration originalConfiguration, String mode) throws CoreException {
        try {
            ILaunchConfiguration configuration = createConfiguration(originalConfiguration);
            delegateClass = configuration.getAttribute(PHPDebugCorePreferenceNames.CONFIGURATION_DELEGATE_CLASS, ""); //$NON-NLS-1$
            MakeGoodLaunch launch = new MakeGoodLaunch(configuration, mode, null);
            TestLifecycle.getInstance().setLaunch(launch);
            setLaunchPerspective(configuration, mode);
            return launch;
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            cancelLaunch();
            throw e;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch();
            throw new CoreException(status);
        }
    }

    /**
     * @since 1.7.0
     */
    private void setLaunchPerspective(ILaunchConfiguration configuration, String mode) throws CoreException {
        HashSet<String> modes = new HashSet<String>();
        modes.add(mode);

        ILaunchConfigurationType launchConfigurationType =
            DebugPlugin.getDefault()
                .getLaunchManager()
                .getLaunchConfigurationType(IPHPDebugConstants.PHPEXELaunchType);
        if (launchConfigurationType == null) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "The launch configuration type for the ID [ " + IPHPDebugConstants.PHPEXELaunchType + " ] is not found.")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        ILaunchDelegate[] launchDelegates = launchConfigurationType.getDelegates(modes);
        if (launchDelegates.length == 0) {
            throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "The delegates capable of launching in the specified modes for the launch configuration type ID [ " + IPHPDebugConstants.PHPEXELaunchType + " ] are not found.")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        DebugUITools.setLaunchPerspective(
            configuration.getType(),
            mode,
            DebugUITools.getLaunchPerspective(launchConfigurationType, launchDelegates[0], modes)
        );
    }

    @Override
    public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
        if (!configuration.exists()) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "The configuration for the launch is not found.")); //$NON-NLS-1$
            cancelLaunch(monitor);
            return false;
        }

        boolean result;
        try {
            result = super.preLaunchCheck(configuration, mode, monitor);
        } catch (DebugException e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            cancelLaunch(monitor);
            throw e;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        }
        if (!result) {
            cancelLaunch(monitor);
        }
        return result;
    }

    @Override
    public void launch(
        ILaunchConfiguration originalConfiguration,
        String mode,
        ILaunch launch,
        IProgressMonitor monitor) throws CoreException {
        ILaunchConfiguration configuration = launch.getLaunchConfiguration();
        if (configuration == null) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "No configuration was launched.")); //$NON-NLS-1$
            cancelLaunch(monitor);
            return;
        }

        if (!configuration.exists()) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "The configuration for the launch is not found.")); //$NON-NLS-1$
            cancelLaunch(monitor);
            return;
        }

        try {
            JUnitXMLRegistry.create();
        } catch (SecurityException e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        }

        try {
            super.launch(configuration, mode, launch, monitor);
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            cancelLaunch(monitor);
            throw e;
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
            Activator.getDefault().getLog().log(status);
            cancelLaunch(monitor);
            throw new CoreException(status);
        }
    }

    @Override
    /**
     * @since 1.2.0
     */
    protected ILaunchConfigurationDelegate2 getConfigurationDelegate(ILaunchConfiguration configuration) throws CoreException {
        if (launchConfigurationDelegate == null) {
            try {
                if (delegateClass.length() == 0) {
                    throw new IllegalArgumentException();
                }

                launchConfigurationDelegate = (ILaunchConfigurationDelegate2) Class
                .forName(delegateClass).newInstance();
            } catch (Throwable t) {
                throw new CoreException(new Status(IStatus.ERROR,
                        PHPDebugPlugin.ID, 0,
                        "Launch configuration delegate loading error.", t));
            }
        }
        return launchConfigurationDelegate;
    }

    private ILaunchConfiguration createConfiguration(
        ILaunchConfiguration configuration) throws CoreException, MethodNotFoundException, ResourceNotFoundException {
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

        String mainScript = TestLifecycle.getInstance().getTestTargets().getMainScript();
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
            generateCommandLine(junitXMLFile)
        );

        IProject project = TestLifecycle.getInstance().getTestTargets().getProject();
        if (project != null && project.exists()) {
            workingCopy.setAttribute(IPHPDebugConstants.PHP_Project, project.getName());
            rewriteBasicConfigurationAttributes(workingCopy, project);
        }

        return workingCopy;
    }

    public static String getCommandPath() throws CoreException {
        return StagehandTestRunner.getCommandPath();
    }

    public static String getJUnitXMLFile(ILaunch launch) throws CoreException {
        return launch.getLaunchConfiguration().getAttribute(MAKEGOOD_JUNIT_XML_FILE, (String) null);
    }

    private void rewriteBasicConfigurationAttributes(ILaunchConfigurationWorkingCopy workingCopy, IProject project) {
        PHPexeItem phpexeItem = new PHPexeItemRepository().findByProject(project);
        if (phpexeItem == null) return;

        workingCopy.setAttribute(PHPDebugCorePreferenceNames.PHP_DEBUGGER_ID, phpexeItem.getDebuggerID());
        workingCopy.setAttribute(PHPDebugCorePreferenceNames.CONFIGURATION_DELEGATE_CLASS, PHPDebuggersRegistry.getDebuggerConfiguration(phpexeItem.getDebuggerID()).getScriptLaunchDelegateClass());
        workingCopy.setAttribute(IPHPDebugConstants.ATTR_EXECUTABLE_LOCATION, phpexeItem.getExecutable().getAbsolutePath().toString());
        workingCopy.setAttribute(IPHPDebugConstants.ATTR_INI_LOCATION, phpexeItem.getINILocation() != null ? phpexeItem.getINILocation().toString() : null);
        workingCopy.setAttribute(IPHPDebugConstants.RUN_WITH_DEBUG_INFO, PHPDebugPlugin.getDebugInfoOption());
        workingCopy.setAttribute(IDebugParametersKeys.FIRST_LINE_BREAKPOINT, PHPProjectPreferences.getStopAtFirstLine(project));
    }

    private void cancelLaunch() {
        TestLifecycle.destroy();
    }

    private void cancelLaunch(IProgressMonitor monitor) {
        monitor.setCanceled(true);
        cancelLaunch();
    }

    /**
     * @since 2.5.0
     */
    private String generateCommandLine(String junitXMLFile) throws CoreException, MethodNotFoundException, ResourceNotFoundException {
        Assert.isNotNull(junitXMLFile, "The JUnit XML file should not be null."); //$NON-NLS-1$

        MakeGoodProperty property = new MakeGoodProperty(TestLifecycle.getInstance().getTestTargets().getFirstResource());
        StringBuilder buffer = new StringBuilder();

        buffer.append(" --no-ansi"); //$NON-NLS-1$
        buffer.append(" " + property.getTestingFramework().name().toLowerCase()); //$NON-NLS-1$

        String preloadScript = property.getPreloadScript();
        if (!preloadScript.equals("")) { //$NON-NLS-1$
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource preloadResource = root.findMember(preloadScript);
            if (preloadResource == null) {
                throw new ResourceNotFoundException("The resource [ " + preloadScript + " ] is not found."); //$NON-NLS-1$ //$NON-NLS-2$
            }

            buffer.append(" -p \"" + preloadResource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        buffer.append(" --log-junit=\"" + junitXMLFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append(" --log-junit-realtime"); //$NON-NLS-1$

        if (RuntimeConfiguration.getInstance().stopsOnFailure) {
            buffer.append(" -s"); //$NON-NLS-1$
        }

        if (property.getTestingFramework() == TestingFramework.PHPUnit) {
            String phpunitConfigFile = property.getPHPUnitConfigFile();
            if (!"".equals(phpunitConfigFile)) { //$NON-NLS-1$
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(phpunitConfigFile);
                if (resource == null) {
                    throw new ResourceNotFoundException("The resource [ " + phpunitConfigFile + " ] is not found."); //$NON-NLS-1$ //$NON-NLS-2$
                }

                buffer.append(" --phpunit-config=\"" + resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else if (property.getTestingFramework() == TestingFramework.CakePHP) {
            String cakephpAppPath = property.getCakePHPAppPath();
            if ("".equals(cakephpAppPath)) { //$NON-NLS-1$
                cakephpAppPath = getDefaultCakePHPAppPath();
            }
            if (!"".equals(cakephpAppPath)) { //$NON-NLS-1$
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(cakephpAppPath);
                if (resource == null) {
                    throw new ResourceNotFoundException("The resource [ " + cakephpAppPath + " ] is not found."); //$NON-NLS-1$ //$NON-NLS-2$
                }

                buffer.append(" --cakephp-app-path=\"" + resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }

            String cakephpCorePath = property.getCakePHPCorePath();
            if (!"".equals(cakephpCorePath)) { //$NON-NLS-1$
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(cakephpCorePath);
                if (resource == null) {
                    throw new ResourceNotFoundException("The resource [ " + cakephpCorePath + " ] is not found."); //$NON-NLS-1$ //$NON-NLS-2$
                }

                buffer.append(" --cakephp-core-path=\"" + resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else if (property.getTestingFramework() == TestingFramework.CIUnit) {
            String ciunitPath = property.getCIUnitPath();
            if ("".equals(ciunitPath)) { //$NON-NLS-1$
                ciunitPath = getDefaultCIUnitPath();
            }
            if (!"".equals(ciunitPath)) { //$NON-NLS-1$
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(ciunitPath);
                if (resource == null) {
                    throw new ResourceNotFoundException("The resource [ " + ciunitPath + " ] is not found."); //$NON-NLS-1$ //$NON-NLS-2$
                }

                buffer.append(" --ciunit-path=\"" + resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }

            String ciunitConfigFile = property.getCIUnitConfigFile();
            if (!"".equals(ciunitConfigFile)) { //$NON-NLS-1$
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(ciunitConfigFile);
                if (resource == null) {
                    throw new ResourceNotFoundException("The resource [ " + ciunitConfigFile + " ] is not found."); //$NON-NLS-1$ //$NON-NLS-2$
                }

                buffer.append(" --phpunit-config=\"" + resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        Set<String> testFiles = new HashSet<String>();
        Set<String> testClasses = new HashSet<String>();
        Set<String> testMethods = new HashSet<String>();
        for (Object testTarget: TestLifecycle.getInstance().getTestTargets().getAll()) {
            IResource resource = TestLifecycle.getInstance().getTestTargets().getResource(testTarget);
            if (resource == null || resource.exists() == false) {
                throw new ResourceNotFoundException("The resource for the test target [ " + testTarget + " ] is not found."); //$NON-NLS-1$ //$NON-NLS-2$
            }

            testFiles.add(resource.getLocation().toString());
            if (testTarget instanceof IType) {
                PHPType phpType = new PHPType((IType) testTarget, property.getTestingFramework());
                if (phpType.isNamespace()) {
                    for (IType type: ((IType) testTarget).getTypes()) {
                        testClasses.add(urlencode(PHPClassType.fromIType(type).getTypeName()));
                    }
                } else if (phpType.isClass()) {
                    testClasses.add(urlencode(PHPClassType.fromIType((IType) testTarget).getTypeName()));
                }
            } else if (testTarget instanceof IMethod) {
                IMethod method = findMethod((IMethod) testTarget);
                if (method == null) {
                    throw new MethodNotFoundException("An unknown method context [ " + testTarget + " ] has been found."); //$NON-NLS-1$ //$NON-NLS-2$
                }
                testMethods.add(
                    urlencode(
                        PHPClassType.fromIType(method.getDeclaringType()).getTypeName() +
                        "::" + //$NON-NLS-1$
                        method.getElementName()
                    )
                );
            } else if (testTarget instanceof ClassTestTarget) {
                testClasses.add(urlencode(((ClassTestTarget) testTarget).getClassName()));
            }
        }

        if (testClasses.size() > 0) {
            for (String testClass: testClasses) {
                buffer.append(" --test-class=\"" + testClass.toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        if (testMethods.size() > 0) {
            for (String testMethod: testMethods) {
                buffer.append(" --test-method=\"" + testMethod.toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        buffer.append(" -R"); //$NON-NLS-1$
        buffer.append(
            " --test-file-pattern=\"" + //$NON-NLS-1$
            (property.getTestFilePattern().equals("") ? property.getTestingFramework().getTestFilePattern() : property.getTestFilePattern()) + //$NON-NLS-1$
            "\"" //$NON-NLS-1$
        );
        for (String testFile: testFiles) {
            buffer.append(" \"" + testFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Debug.println(buffer.toString());

        return buffer.toString();
    }

    /**
     * @since 2.5.0
     */
    private String getDefaultCakePHPAppPath() {
        Assert.isNotNull(TestLifecycle.getInstance().getTestTargets().getProject(), "One or more test targets should be added."); //$NON-NLS-1$

        IResource resource = TestLifecycle.getInstance().getTestTargets().getProject().findMember("/app"); //$NON-NLS-1$
        if (resource == null) return ""; //$NON-NLS-1$

        return resource.getFullPath().toString();
    }

    /**
     * @since 2.5.0
     */
    private String getDefaultCIUnitPath() {
        Assert.isNotNull(TestLifecycle.getInstance().getTestTargets().getProject(), "One or more test targets should be added."); //$NON-NLS-1$

        IResource resource = TestLifecycle.getInstance().getTestTargets().getProject().findMember("/system/application/tests"); //$NON-NLS-1$
        if (resource == null) return ""; //$NON-NLS-1$

        return resource.getFullPath().toString();
    }

    /**
     * @since 2.5.0
     */
    private String urlencode(String subject) throws CoreException
    {
        try {
            return URLEncoder.encode(subject, TestLifecycle.getInstance().getTestTargets().getProject().getDefaultCharset());
        } catch (UnsupportedEncodingException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));

            return subject;
        }
    }

    /**
     * @since 2.5.0
     */
    private IMethod findMethod(IMethod method) {
        IModelElement parent = method.getParent();
        if (parent == null) return null;
        if (parent instanceof IType) {
            return method;
        }

        while (true) {
            if (parent instanceof IMethod) {
                return findMethod((IMethod) parent);
            }

            parent = parent.getParent();
            if (parent == null) return null;
        }
    }
}
