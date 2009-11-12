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
import com.piece_framework.makegood.ui.parser.EditorParser;

public class RunTestFromEditorInClass extends RunTestFromEditor {
    @Override
    public boolean isEnabled() {
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
