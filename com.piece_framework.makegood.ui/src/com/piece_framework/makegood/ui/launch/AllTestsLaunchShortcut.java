package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.launch.MakeGoodLaunchParameter;

public class AllTestsLaunchShortcut extends MakeGoodLaunchShortcut {

    @Override
    public void launch(ISelection selection, String mode) {
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }

        Object target = ((IStructuredSelection) selection).getFirstElement();
        IResource resource = null;
        if (target instanceof IModelElement) {
            resource = ((IModelElement) target).getResource();
        } else if (target instanceof IResource) {
            resource = (IResource) target;
        }
        if (resource == null) {
            return;
        }

        MakeGoodLaunchParameter parameter = addTestFolders(resource);
        ISelection element = new StructuredSelection(parameter.getMainScriptResource());

        super.launch(element, mode);
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        if (!(editor.getEditorInput() instanceof IFileEditorInput)) {
            return;
        }
        IFile target = ((IFileEditorInput) editor.getEditorInput()).getFile();
        if (!PHPResource.isTrue(target)) {
            ISelection selection = new StructuredSelection(target);
            launch(selection, mode);
            return;
        }

        addTestFolders(target);

        super.launch(editor, mode);
    }

    private MakeGoodLaunchParameter addTestFolders(IResource resource) {
        MakeGoodProperty property = new MakeGoodProperty(resource);
        MakeGoodLaunchParameter parameter = MakeGoodLaunchParameter.getInstance();
        parameter.clearTargets();
        for (IFolder testFolder: property.getTestFolders()) {
            parameter.addTarget(testFolder);
        }
        return parameter;
    }
}
