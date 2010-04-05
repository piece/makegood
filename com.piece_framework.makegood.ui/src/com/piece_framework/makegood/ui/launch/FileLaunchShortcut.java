package com.piece_framework.makegood.ui.launch;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.ui.IEditorPart;

public class FileLaunchShortcut extends NoSearchLaunchShortcut {
    @Override
    protected IModelElement getTargetElement(IEditorPart editor) {
        EditorParser parser = new EditorParser(editor);
        return parser.getSourceModule();
    }
}
