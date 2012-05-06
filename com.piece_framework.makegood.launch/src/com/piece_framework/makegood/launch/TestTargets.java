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

import org.eclipse.core.internal.content.ContentTypeManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.php.internal.core.documentModel.provisional.contenttype.ContentTypeIdForPHP;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.core.PHPFlags;
import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.core.TestingFramework;

public class TestTargets {
    private List<Object> targets = new ArrayList<Object>();

    /**
     * @since 1.3.0
     */
    private IProject project;

    /**
     * @since 1.3.0
     */
    private static TestTargets soleInstance;

    /**
     * @since 1.3.0
     */
    public static TestTargets getInstance() {
        if (soleInstance == null) {
            soleInstance = new TestTargets();
        }
        return soleInstance;
    }

    /**
     * @since 1.3.0
     */
    private TestTargets() {
        super();
    }

    public void add(Object target) throws ResourceNotFoundException, ProjectNotFoundException {
        targets.add(target);
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

    public String generateCommandLine(String junitXMLFile) throws CoreException, MethodNotFoundException {
        StringBuilder buffer = new StringBuilder();

        buffer.append(" --no-ansi"); //$NON-NLS-1$
        buffer.append(" " + getTestingFramework().name().toLowerCase()); //$NON-NLS-1$

        String preloadScript = getPreloadScript();
        if (!preloadScript.equals("")) { //$NON-NLS-1$
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource preloadResource = root.findMember(preloadScript);
            if (preloadResource != null) {
                buffer.append(" -p \"" + preloadResource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        if (junitXMLFile != null) {
            buffer.append(" --log-junit=\"" + junitXMLFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        buffer.append(" --log-junit-realtime"); //$NON-NLS-1$

        if (RuntimeConfiguration.getInstance().stopsOnFailure) {
            buffer.append(" -s"); //$NON-NLS-1$
        }

        if (getTestingFramework() == TestingFramework.PHPUnit) {
            String phpunitConfigFile = getPHPUnitConfigFile();
            if (!"".equals(phpunitConfigFile)) { //$NON-NLS-1$
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(phpunitConfigFile);
                if (resource != null) {
                    buffer.append(" --phpunit-config=\"" + resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        } else if (getTestingFramework() == TestingFramework.CakePHP) {
            String cakephpAppPath = getCakePHPAppPath();
            if ("".equals(cakephpAppPath)) { //$NON-NLS-1$
                cakephpAppPath = getDefaultCakePHPAppPath();
            }
            if (!"".equals(cakephpAppPath)) { //$NON-NLS-1$
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(cakephpAppPath);
                if (resource != null) {
                    buffer.append(" --cakephp-app-path=\"" + resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }

            String cakephpCorePath = getCakePHPCorePath();
            if (!"".equals(cakephpCorePath)) { //$NON-NLS-1$
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(cakephpCorePath);
                if (resource != null) {
                    buffer.append(" --cakephp-core-path=\"" + resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        } else if (getTestingFramework() == TestingFramework.CIUnit) {
            String ciunitPath = getCIUnitPath();
            if ("".equals(ciunitPath)) { //$NON-NLS-1$
                ciunitPath = getDefaultCIUnitPath();
            }
            if (!"".equals(ciunitPath)) { //$NON-NLS-1$
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(ciunitPath);
                if (resource != null) {
                    buffer.append(" --ciunit-path=\"" + resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }

            String ciunitConfigFile = getCIUnitConfigFile();
            if (!"".equals(ciunitConfigFile)) { //$NON-NLS-1$
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(ciunitConfigFile);
                if (resource != null) {
                    buffer.append(" --phpunit-config=\"" + resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }

        Set<String> testFiles = new HashSet<String>();
        Set<String> testClasses = new HashSet<String>();
        Set<String> testMethods = new HashSet<String>();
        for (Object target: targets) {
            testFiles.add(getResource(target).getLocation().toString());
            if (target instanceof IType) {
                int flags = ((IType) target).getFlags();
                if (PHPFlags.isNamespace(flags)) {
                    for (IType type: ((IType) target).getTypes()) {
                        testClasses.add(urlencode(PHPClassType.fromIType(type).getTypeName()));
                    }
                } else if (PHPFlags.isClass(flags)) {
                    testClasses.add(urlencode(PHPClassType.fromIType((IType) target).getTypeName()));
                }
            } else if (target instanceof IMethod) {
                IMethod method = findMethod((IMethod) target);
                if (method == null) {
                    throw new MethodNotFoundException("An unknown method context [ " + target + " ] has been found."); //$NON-NLS-1$ //$NON-NLS-2$
                }
                testMethods.add(
                    urlencode(
                        PHPClassType.fromIType(method.getDeclaringType()).getTypeName() +
                        "::" + //$NON-NLS-1$
                        method.getElementName()
                    )
                );
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
        for (String testFile: testFiles) {
            buffer.append(" \"" + testFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Debug.println(buffer.toString());

        return buffer.toString();
    }

    public TestingFramework getTestingFramework() {
        return createMakeGoodProperty().getTestingFramework();
    }

    private IFile findDummyFile(IFolder folder) {
        try {
            for (IResource resource: folder.members()) {
                if (PHPResource.isPHPSource(resource)) {
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

    private String getPreloadScript() {
        return createMakeGoodProperty().getPreloadScript();
    }

    private IResource getResource(Object target) {
        IResource resource = null;
        if (target instanceof IModelElement) {
            resource = ((IModelElement) target).getResource();
        } else if (target instanceof IResource) {
            resource = (IResource) target;
        }
        return resource;
    }

    private IResource getFirstResource() {
        if (getCount() == 0) return null;
        return getResource(targets.get(0));
    }

    private String getPHPUnitConfigFile() {
        return createMakeGoodProperty().getPHPUnitConfigFile();
    }

    private String getCakePHPAppPath() {
        return createMakeGoodProperty().getCakePHPAppPath();
    }

    private String getDefaultCakePHPAppPath() {
        Assert.isNotNull(project, "One or more test targets should be added."); //$NON-NLS-1$

        IResource resource = project.findMember("/app"); //$NON-NLS-1$
        if (resource == null) return ""; //$NON-NLS-1$
        return resource.getFullPath().toString();
    }

    private String getCakePHPCorePath() {
        return createMakeGoodProperty().getCakePHPCorePath();
    }

    /**
     * @since 1.3.0
     */
    private String getCIUnitPath() {
        return createMakeGoodProperty().getCIUnitPath();
    }

    /**
     * @since 1.3.0
     */
    private String getDefaultCIUnitPath() {
        Assert.isNotNull(project, "One or more testing targets should be added."); //$NON-NLS-1$

        IResource resource = project.findMember("/system/application/tests"); //$NON-NLS-1$
        if (resource == null) return ""; //$NON-NLS-1$
        return resource.getFullPath().toString();
    }

    /**
     * @since 1.3.0
     */
    private String getCIUnitConfigFile() {
        return createMakeGoodProperty().getCIUnitConfigFile();
    }

    private MakeGoodProperty createMakeGoodProperty() {
        return new MakeGoodProperty(getFirstResource());
    }

    private String getEncoding()
    {
        // TODO use the encoding of the current contents instead of the default charset.
        IContentType contentType = ContentTypeManager.getInstance().getContentType(ContentTypeIdForPHP.ContentTypeID_PHP);
        if (contentType == null) return ResourcesPlugin.getEncoding();
        String defaultCharset = contentType.getDefaultCharset();
        if (defaultCharset == null) return ResourcesPlugin.getEncoding();
        return defaultCharset;
    }

    private String urlencode(String subject)
    {
        try {
            return URLEncoder.encode(subject, getEncoding());
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
        targets.clear();
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
        return targets.size();
    }
}
