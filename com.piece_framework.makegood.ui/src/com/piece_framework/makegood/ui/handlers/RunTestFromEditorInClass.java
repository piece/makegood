package com.piece_framework.makegood.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.ui.launch.EditorParser;
import com.piece_framework.makegood.ui.launch.TestRunner;

public class RunTestFromEditorInClass extends RunTestFromEditor {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        TestRunner.runTestsInClass(HandlerUtil.getActiveEditor(event));
        return null;
    }

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
}
