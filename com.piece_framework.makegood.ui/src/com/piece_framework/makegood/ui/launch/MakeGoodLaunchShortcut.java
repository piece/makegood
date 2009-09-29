package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut;

public class MakeGoodLaunchShortcut extends PHPExeLaunchShortcut {
    @Override
    public void launch(ISelection selection, String mode) {
        ISelection element = selection;
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            IFolder folder = null;
            if (structuredSelection.getFirstElement() instanceof IScriptFolder) {
                IScriptFolder scriptFolder = (IScriptFolder) structuredSelection.getFirstElement();
                folder = (IFolder) scriptFolder.getResource();
            } else if (structuredSelection.getFirstElement() instanceof IFolder) {
                folder = (IFolder) structuredSelection.getFirstElement();
            }
            element = new StructuredSelection(findDummyFile(folder));
        }
        super.launch(element, mode);
    }

    private IFile findDummyFile(IFolder folder) {
        try {
            for (IResource resource: folder.members()) {
                if (resource instanceof IFile
                    && resource.getFileExtension().equalsIgnoreCase("php")
                    ) {
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
    
    @Override
    protected ILaunchConfigurationType getPHPExeLaunchConfigType() {
        ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
        return lm.getLaunchConfigurationType("com.piece_framework.makegood.launch.launchConfigurationType");
    }
}
