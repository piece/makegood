package com.piece_framework.makegood.ui.handlers;

import org.eclipse.dltk.core.IModelElement;

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
        if (element.getElementType() == IModelElement.TYPE
            || element.getElementType() == IModelElement.METHOD
            || element.getElementType() == IModelElement.FIELD
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
