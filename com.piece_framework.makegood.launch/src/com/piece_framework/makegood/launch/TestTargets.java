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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

import com.piece_framework.makegood.core.Resource;

public class TestTargets {
    private List<Object> testTargets = new ArrayList<Object>();

    /**
     * @since 1.3.0
     */
    private IProject project;

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

    private IFile findDummyFile(IFolder folder) {
        try {
            for (IResource resource: folder.members()) {
                if (new Resource(resource).isPHPFile()) {
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

    IResource getResource(Object testTarget) {
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

    IResource getFirstResource() {
        if (getCount() == 0) return null;
        return getResource(testTargets.get(0));
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
    public int getCount() {
        return testTargets.size();
    }

    /**
     * @since 2.5.0
     */
    public List<Object> getAll() {
         return Collections.unmodifiableList(testTargets);
    }
}
