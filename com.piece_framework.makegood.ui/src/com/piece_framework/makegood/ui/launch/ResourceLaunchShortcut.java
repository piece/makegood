package com.piece_framework.makegood.ui.launch;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import com.piece_framework.makegood.launch.MakeGoodLaunchParameter;

public class ResourceLaunchShortcut extends MakeGoodLaunchShortcut {
    @Override
    public void launch(ISelection selection, String mode) {
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }

        Object target = ((IStructuredSelection) selection).getFirstElement();
        MakeGoodLaunchParameter parameter = MakeGoodLaunchParameter.getInstance();
        parameter.clearTargets();
        parameter.addTarget(target);

        ISelection element = new StructuredSelection(parameter.getMainScriptResource());
        super.launch(element, mode);
    }
}
