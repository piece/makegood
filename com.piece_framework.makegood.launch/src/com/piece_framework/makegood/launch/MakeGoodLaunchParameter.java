package com.piece_framework.makegood.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;

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

    public IResource getScriptResource() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        return root.findMember(getScript());
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

    public String generateParameter(String log) {
        StringBuilder buffer = new StringBuilder();

        String preloadScript = getPreloadScript();
        if (!preloadScript.equals("")) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource preloadResource = root.findMember(preloadScript);
            if (preloadResource != null) {
                buffer.append("-p " + preloadResource.getLocation().toString());
            }
        }

        if (log != null) {
            buffer.append(" --log-junit=" + log);
        }

        if (target instanceof IType) {
            String targetValue = ((IType) target).getElementName();
            buffer.append(" --classes=" + targetValue);
        }
        if (target instanceof IMethod) {
            IMethod method = (IMethod) target;
            String targetValue = method.getParent().getElementName() + "::" + method.getElementName();
            buffer.append(" -m " + targetValue);
        }

        if (getTargetResource() instanceof IFolder) {
            buffer.append(" -R");
        }
        buffer.append(" " + getTargetResource().getLocation().toString());

        return buffer.toString();
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
