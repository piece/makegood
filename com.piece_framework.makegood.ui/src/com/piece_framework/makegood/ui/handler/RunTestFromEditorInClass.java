package com.piece_framework.makegood.ui.handler;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.ui.launch.MakeGoodLaunchShortcut;

public class RunTestFromEditorInClass extends RunTestFromEditor {
    @Override
    public boolean isEnabled() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart editor = page.getActiveEditor();

        ISourceModule source = EditorUtility.getEditorInputModelElement(editor, false);
        if (source == null) {
            return false;
        }

        ITextEditor textEditor = (ITextEditor) editor;
        ISelectionProvider provider = (ISelectionProvider) textEditor.getSelectionProvider();
        ITextSelection selection = (ITextSelection) provider.getSelection();
        int offset = selection.getOffset();

        IModelElement element = null;
        try {
            ScriptModelUtil.reconcile(source);
            element = source.getElementAt(offset);
        } catch (ModelException e) {
        }

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
