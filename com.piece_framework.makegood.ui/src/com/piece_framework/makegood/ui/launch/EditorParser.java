package com.piece_framework.makegood.ui.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.ui.Activator;

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

    public IModelElement getModelElementOnSelection() {
        ITextEditor textEditor = (ITextEditor) editor;
        ISelectionProvider provider = (ISelectionProvider) textEditor.getSelectionProvider();
        ITextSelection selection = (ITextSelection) provider.getSelection();
        int offset = selection.getOffset();

        ISourceModule source = getSourceModule();
        IModelElement element = null;
        try {
            ScriptModelUtil.reconcile(source);
            element = source.getElementAt(offset);
        } catch (ModelException e) {
            Activator.getDefault().getLog().log(
                new Status(
                    Status.WARNING,
                    Activator.PLUGIN_ID,
                    e.getMessage(),
                    e
                )
            );
        }
        return element;
    }

    public List<IType> getTypes() {
        ISourceModule source = getSourceModule();
        if (source == null) {
            return null;
        }
        List<IType> types = new ArrayList<IType>();
        try {
            for (IType type: source.getAllTypes()) {
                types.add(type);
                ITypeHierarchy hierarchy = type.newTypeHierarchy(new NullProgressMonitor());
                if (hierarchy != null) {
                    for (IType subClass : hierarchy.getAllSubtypes(type)) {
                        types.add(subClass);
                    }
                }
            }
        } catch (ModelException e) {
            Activator.getDefault().getLog().log(
                new Status(
                    Status.ERROR,
                    Activator.PLUGIN_ID,
                    e.getMessage(),
                    e
                )
            );
        }
        return types;
    }
}
