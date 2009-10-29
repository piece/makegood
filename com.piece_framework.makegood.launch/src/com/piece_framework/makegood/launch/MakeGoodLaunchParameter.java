package com.piece_framework.makegood.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;

import com.piece_framework.makegood.core.PHPResource;

public class MakeGoodLaunchParameter {
    private static MakeGoodLaunchParameter parameter;
    private Object target;

    private MakeGoodLaunchParameter() {
    }

    public static MakeGoodLaunchParameter get() {
        if (parameter == null) {
            parameter = new MakeGoodLaunchParameter();
        }
        return parameter;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }

    public String getScript() {
        boolean isFolder = target instanceof IProjectFragment
                           || target instanceof IScriptFolder
                           || target instanceof IFolder;
        boolean isFile = target instanceof IFile;
        boolean isModelElement = target instanceof IModelElement;

        IFile file = null;
        if (isFolder) {
            IFolder folder = null;
            if (target instanceof IModelElement) {
                folder = (IFolder) ((IModelElement) target).getResource();
            } else if (target instanceof IFolder) {
                folder = (IFolder) target;
            }
            file = findDummyFile(folder);
        } else if (isFile) {
            file = (IFile) target;
        } else if (isModelElement) {
            file = (IFile) ((IModelElement) target).getResource();
        }

        if (file != null) {
            return file.getFullPath().toString();
        }
        return null;
    }

    private IFile findDummyFile(IFolder folder) {
        try {
            for (IResource resource: folder.members()) {
                if (PHPResource.isTrue(resource)) {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
