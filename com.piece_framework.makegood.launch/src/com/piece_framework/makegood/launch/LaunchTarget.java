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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.osgi.framework.debug.Debug;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.core.TestingFramework;

public class LaunchTarget {
    private static LaunchTarget soleInstance;
    private List<Object> targets;

    private LaunchTarget() {
    }

    public static LaunchTarget getInstance() {
        if (soleInstance == null) {
            soleInstance = new LaunchTarget();
        }
        return soleInstance;
    }

    public void addTarget(Object object) {
        targets.add(object);
    }

    public void clearTargets() {
        targets = new ArrayList<Object>();
    }

    public String getMainScript() {
        IFile file = null;

        IResource resource = getTargetResource(targets.get(0));
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
                buffer.append("-p \"" + preloadResource.getLocation().toString() + "\""); //$NON-NLS-1$
            }
        }

        if (junitXMLFile != null) {
            buffer.append(" --log-junit=\"" + junitXMLFile + "\""); //$NON-NLS-1$
        }

        buffer.append(" --log-junit-realtime");

        if (RuntimeConfiguration.getInstance().stopsOnFailure) {
            buffer.append(" --stop-on-failure");
        }

        StringBuilder classes = new StringBuilder();
        StringBuilder methods = new StringBuilder();
        StringBuilder resources = new StringBuilder();
        for (Object target: targets) {
            if (target instanceof ISourceModule) {
                try {
                    for (IType type: ((ISourceModule) target).getAllTypes()) {
                        String targetValue = type.getElementName();
                        classes.append(classes.length() > 0 ? "," : ""); //$NON-NLS-1$ //$NON-NLS-2$
                        classes.append(targetValue);
                    }
                } catch (ModelException e) {
                    Activator.getDefault().getLog().log(
                        new Status(
                            Status.WARNING,
                            Activator.PLUGIN_ID,
                            e.getMessage(),
                            e
                        )
                    );
                }
            }
            if (target instanceof IType) {
                String targetValue = ((IType) target).getElementName();
                classes.append(classes.length() > 0 ? "," : ""); //$NON-NLS-1$ //$NON-NLS-2$
                classes.append(targetValue);
            }
            if (target instanceof IMethod) {
                IMethod method = (IMethod) target;
                String targetValue = method.getParent().getElementName() + "::" + //$NON-NLS-1$
                                     method.getElementName();
                methods.append(methods.length() > 0 ? "," : ""); //$NON-NLS-1$ //$NON-NLS-2$
                methods.append(targetValue);
            }

            resources.append(" \"" + getTargetResource(target).getLocation().toString() + "\""); //$NON-NLS-1$
        }

        buffer.append(classes.length() > 0 ?
                      " --classes " + classes.toString() : ""); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append(methods.length() > 0 ?
                      " -m " + methods.toString() : ""); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append(" -R " + resources.toString()); //$NON-NLS-1$
        Debug.println(buffer.toString());
        return buffer.toString();
    }

    public TestingFramework getTestingFramework() {
        MakeGoodProperty property = new MakeGoodProperty(getTargetResource(targets.get(0)));
        return property.getTestingFramework();
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
        MakeGoodProperty property = new MakeGoodProperty(getTargetResource(targets.get(0)));
        return property.getPreloadScript();
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
}
