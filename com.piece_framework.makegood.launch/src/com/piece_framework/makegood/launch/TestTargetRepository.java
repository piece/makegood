/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2012 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.launch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

import com.piece_framework.makegood.core.Resource;
import com.piece_framework.makegood.core.PHPType;
import com.piece_framework.makegood.core.TestingFramework;
import com.piece_framework.makegood.core.preference.MakeGoodProperty;

public class TestTargetRepository {
    private List<Object> testTargets = new ArrayList<Object>();

    /**
     * @since 1.3.0
     */
    private IProject project;

    /**
     * @since 1.3.0
     */
    private static TestTargetRepository soleInstance;

    /**
     * @since 1.3.0
     */
    public static TestTargetRepository getInstance() {
        if (soleInstance == null) {
            soleInstance = new TestTargetRepository();
        }
        return soleInstance;
    }

    /**
     * @since 1.3.0
     */
    private TestTargetRepository() {
        super();
    }

    public void add(Object testTarget) throws ResourceNotFoundException, ProjectNotFoundException, ModelException {
        testTargets.add(testTarget);

        if (getCount() == 1) {
            IResource resource = getFirstResource();
            if (resource == null) {
                throw new ResourceNotFoundException("The resource is not found. The given target may be invalid."); //$NON-NLS-1$
            }
            IProject project = resource.getProject();
            if (project == null) {
                throw new ProjectNotFoundException("The project is not found. The given resource may be the workspace root."); //$NON-NLS-1$
            }
            this.project = project;
        }

        if (testTarget instanceof ISourceModule) {
            for (IType type: ((ISourceModule) testTarget).getAllTypes()) {
                testTargets.add(type);
            }
        }
    }

    public String getMainScript() {
        IFile file = null;

        IResource resource = getFirstResource();
        if (resource instanceof IFolder) {
            file = findDummyFile((IFolder) resource);
        } else if (resource instanceof IFile) {
            file = (IFile) resource;
        }

        if (file == null) return null;

        return file.getFullPath().toString();
    }

    public IResource getMainScriptResource() {
        String mainScript = getMainScript();
        if (mainScript == null) return null;
        return ResourcesPlugin.getWorkspace().getRoot().findMember(mainScript);
    }

    public String generateCommandLine(String junitXMLFile) throws CoreException, MethodNotFoundException, ResourceNotFoundException {
        Assert.isNotNull(junitXMLFile, "The JUnit XML file should not be null."); //$NON-NLS-1$

        MakeGoodProperty property = new MakeGoodProperty(getFirstResource());
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
        for (Object testTarget: testTargets) {
            IResource resource = getResource(testTarget);
            if (resource == null || resource.exists() == false) {
                throw new ResourceNotFoundException("The resource [ " + testTarget + " ] is not found."); //$NON-NLS-1$ //$NON-NLS-2$
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

    private IFile findDummyFile(IFolder folder) {
        try {
            for (IResource resource: folder.members()) {
                if (new Resource(resource).isPHPSource()) {
                    return (IFile) resource;
                }
            }
            for (IResource resource: folder.members()) {
                if (resource instanceof IFolder) {
                    IFile file = findDummyFile((IFolder) resource);
                    if (file != null) {
                        return file;
                    }
                }
            }
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(
                new Status(
                    Status.WARNING,
                    Activator.PLUGIN_ID,
                    e.getMessage(),
                    e
                )
            );
        }
        return null;
    }

    private IResource getResource(Object testTarget) {
        IResource resource = null;
        if (testTarget instanceof IModelElement) {
            resource = ((IModelElement) testTarget).getResource();
        } else if (testTarget instanceof IResource) {
            resource = (IResource) testTarget;
        } else if (testTarget instanceof ClassTestTarget) {
            resource = ((ClassTestTarget) testTarget).getResource();
        }
        return resource;
    }

    private IResource getFirstResource() {
        if (getCount() == 0) return null;
        return getResource(testTargets.get(0));
    }

    private String getDefaultCakePHPAppPath() {
        Assert.isNotNull(project, "One or more test targets should be added."); //$NON-NLS-1$

        IResource resource = project.findMember("/app"); //$NON-NLS-1$
        if (resource == null) return ""; //$NON-NLS-1$
        return resource.getFullPath().toString();
    }

    /**
     * @since 1.3.0
     */
    private String getDefaultCIUnitPath() {
        Assert.isNotNull(project, "One or more test targets should be added."); //$NON-NLS-1$

        IResource resource = project.findMember("/system/application/tests"); //$NON-NLS-1$
        if (resource == null) return ""; //$NON-NLS-1$
        return resource.getFullPath().toString();
    }

    private String urlencode(String subject) throws CoreException
    {
        try {
            return URLEncoder.encode(subject, getProject().getDefaultCharset());
        } catch (UnsupportedEncodingException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return subject;
        }
    }

    /**
     * @since 1.3.0
     */
    public void clear() {
        project = null;
        testTargets.clear();
    }

    /**
     * @since 1.3.0
     */
    public IProject getProject() {
        return project;
    }

    /**
     * @since 1.3.0
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

    /**
     * @since 1.3.0
     */
    public int getCount() {
        return testTargets.size();
    }
}
