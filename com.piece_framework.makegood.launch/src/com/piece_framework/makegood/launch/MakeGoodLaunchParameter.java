package com.piece_framework.makegood.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IModelElement;

import com.piece_framework.makegood.core.MakeGoodProperty;
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
        IFile file = null;

        IResource resource = getTargetResource();
        if (resource instanceof IFolder) {
            file = findDummyFile((IFolder) resource);
        } else if (resource instanceof IFile) {
            file = (IFile) resource;
        }

        if (file != null) {
            return file.getFullPath().toString();
        }
        return null;
    }

    public String getPreloadScript() {
        MakeGoodProperty property = new MakeGoodProperty(getTargetResource());
        return property.getPreloadScript();
    }

    public IResource getTargetResource() {
        IResource resource = null;
        if (target instanceof IModelElement) {
            resource = ((IModelElement) target).getResource();
        } else if (target instanceof IResource) {
            resource = (IResource) target;
        }
        return resource;
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
