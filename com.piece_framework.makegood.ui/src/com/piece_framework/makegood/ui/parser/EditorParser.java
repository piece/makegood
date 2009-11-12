package com.piece_framework.makegood.ui.parser;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class EditorParser {
    private IEditorPart editor;

    public EditorParser() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        this.editor = page.getActiveEditor();
    }

    public EditorParser(IEditorPart editor) {
        this.editor = editor;
    }

    public ISourceModule getSourceModule() {
        return EditorUtility.getEditorInputModelElement(editor, false);
    }
}
