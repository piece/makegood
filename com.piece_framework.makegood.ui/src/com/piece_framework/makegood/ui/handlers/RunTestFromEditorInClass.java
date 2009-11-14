package com.piece_framework.makegood.ui.handlers;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;

import com.piece_framework.makegood.ui.launch.EditorParser;
import com.piece_framework.makegood.ui.launch.MakeGoodLaunchShortcut;

public class RunTestFromEditorInClass extends RunTestFromEditor {
    @Override
    public boolean isEnabled() {
        if (!super.isEnabled()) {
            return false;
        }

        EditorParser parser = new EditorParser();
        IModelElement element = parser.getModelElementOnSelection();
        if (element instanceof IType
            || element instanceof IMethod
            ) {
            return true;
        }
        return false;
    }

    @Override
    protected int getRunLevel() {
        return MakeGoodLaunchShortcut.RUN_TESTS_ON_CLASS;
    }
}
