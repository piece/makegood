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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.content.ContentTypeManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.php.internal.core.documentModel.provisional.contenttype.ContentTypeIdForPHP;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.core.PHPFlags;
import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.core.TestingFramework;

public class TestingTargets {
    private List<Object> targets = new ArrayList<Object>();

    public void add(Object object) {
        targets.add(object);
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

    public String getProgramArguments(String junitXMLFile) {
        StringBuilder buffer = new StringBuilder();

        String preloadScript = getPreloadScript();
        if (!preloadScript.equals("")) { //$NON-NLS-1$
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource preloadResource = root.findMember(preloadScript);
            if (preloadResource != null) {
                buffer.append("-p \"" + preloadResource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        if (junitXMLFile != null) {
            buffer.append(" --log-junit=\"" + junitXMLFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        buffer.append(" --log-junit-realtime"); //$NON-NLS-1$

        if (RuntimeConfiguration.getInstance().stopsOnFailure) {
            buffer.append(" --stop-on-failure"); //$NON-NLS-1$
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
        }

        StringBuilder classes = new StringBuilder();
        StringBuilder methods = new StringBuilder();
        StringBuilder resources = new StringBuilder();
        for (Object target: targets) {
            resources.append(" \"" + getTargetResource(target).getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            if (target instanceof IType) {
                int flags;
                try {
                    flags = ((IType) target).getFlags();
                } catch (ModelException e) {
                    Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                    continue;
                }

                if (PHPFlags.isNamespace(flags)) {
                    IType[] types;
                    try {
                        types = ((IType) target).getTypes();
                    } catch (ModelException e) {
                        Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                        continue;
                    }
                    for (IType type: types) {
                        if (classes.length() > 0) {
                            classes.append(","); //$NON-NLS-1$
                        }

                        classes.append(urlencode(PHPClassType.fromIType(type).getTypeName()));
                    }
                } else if (PHPFlags.isClass(flags)) {
                    if (classes.length() > 0) {
                        classes.append(","); //$NON-NLS-1$
                    }

                    classes.append(urlencode(PHPClassType.fromIType((IType) target).getTypeName()));
                }
            } else if (target instanceof IMethod) {
                if (methods.length() > 0) {
                    methods.append(","); //$NON-NLS-1$
                }

                methods.append(
                    urlencode(
                        PHPClassType.fromIType(((IMethod) target).getDeclaringType()).getTypeName() +
                        "::" + //$NON-NLS-1$
                        ((IMethod) target).getElementName()
                    )
                );
            }
        }

        if (classes.length() > 0) {
            buffer.append(" --classes=\"" + classes.toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (methods.length() > 0) {
            buffer.append(" -m \"" + methods.toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }

        buffer.append(" -R " + resources.toString()); //$NON-NLS-1$
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

    private IResource getTargetResource(Object target) {
        IResource resource = null;
        if (target instanceof IModelElement) {
            resource = ((IModelElement) target).getResource();
        } else if (target instanceof IResource) {
            resource = (IResource) target;
        }
        return resource;
    }

    private IResource getFirstResource() {
        return getResource(targets.get(0));
    }

    private String getPHPUnitConfigFile() {
        return createMakeGoodProperty().getPHPUnitConfigFile();
    }

    private String getCakePHPAppPath() {
        return createMakeGoodProperty().getCakePHPAppPath();
    }

    private String getDefaultCakePHPAppPath() {
        IResource resource = createMakeGoodProperty().getProject().findMember("/app"); //$NON-NLS-1$
        if (resource == null) return ""; //$NON-NLS-1$
        return resource.getFullPath().toString();
    }

    private String getCakePHPCorePath() {
        return createMakeGoodProperty().getCakePHPCorePath();
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
}
