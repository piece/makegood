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
        IModelElement target = parser.getModelElementOnSelection();
        if (target == null) {
            return parser.getSourceModule();
        }

        if (target.getElementType() == IModelElement.FIELD) {
            target = target.getParent();
        }

        lastTarget = target;
        return target;
    }
}
