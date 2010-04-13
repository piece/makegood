package com.piece_framework.makegood.ui.launch;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.ui.IEditorPart;

public class ContextLaunchShortcut extends NoSearchLaunchShortcut {
    private IModelElement lastTarget;

    @Override
    protected IModelElement getTarget(IEditorPart editor) {
        if (lastTarget != null) {
            return lastTarget;
        }

        EditorParser parser = new EditorParser(editor);
        IModelElement element = parser.getModelElementOnSelection();
        if (element == null) {
            return parser.getSourceModule();
        }
        if (element.getElementType() == IModelElement.FIELD) {
            element = element.getParent();
        }

        lastTarget = element;
        return element;
    }
}
