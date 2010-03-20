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

public class MakeGoodLaunchParameter {
    private static MakeGoodLaunchParameter parameter;
    private List<Object> targets;
    private boolean stopsOnFailure = false;

    private MakeGoodLaunchParameter() {
    }

    public static MakeGoodLaunchParameter getInstance() {
        if (parameter == null) {
            parameter = new MakeGoodLaunchParameter();
        }
        return parameter;
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

        if (file != null) {
            return file.getFullPath().toString();
        }
        return null;
    }

    public IResource getMainScriptResource() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        return root.findMember(getMainScript());
    }

    public String generateParameter(String log) {
        StringBuilder buffer = new StringBuilder();

        String preloadScript = getPreloadScript();
        if (!preloadScript.equals("")) { //$NON-NLS-1$
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource preloadResource = root.findMember(preloadScript);
            if (preloadResource != null) {
                buffer.append("-p \"" + preloadResource.getLocation().toString() + "\""); //$NON-NLS-1$
            }
        }

        if (log != null) {
            buffer.append(" --log-junit=\"" + log + "\""); //$NON-NLS-1$
        }

        buffer.append(" --log-junit-realtime");

        if (parameter.stopsOnFailure) {
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

    public void setStopsOnFailure(boolean stopsOnFailure) {
        this.stopsOnFailure = stopsOnFailure;
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
