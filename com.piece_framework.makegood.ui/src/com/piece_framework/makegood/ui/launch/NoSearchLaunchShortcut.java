package com.piece_framework.makegood.ui.launch;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.launch.MakeGoodLaunchParameter;

public abstract class NoSearchLaunchShortcut extends MakeGoodLaunchShortcut {
    @Override
    public void launch(IEditorPart editor, String mode) {
        if (!(editor instanceof ITextEditor)) {
            return;
        }

        MakeGoodLaunchParameter parameter = MakeGoodLaunchParameter.getInstance();
        parameter.clearTargets();
        parameter.addTarget(getTargetElement(editor));

        super.launch(editor, mode);
    }

    protected abstract IModelElement getTargetElement(IEditorPart editor);
}
