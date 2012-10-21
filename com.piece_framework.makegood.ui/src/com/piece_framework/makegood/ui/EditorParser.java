/**
 * Copyright (c) 2009-2012 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;


public class EditorParser {
    private IEditorPart editor;

    /**
     * @since 2.2.0
     */
    public static EditorParser createActiveEditorParser() {
        ActiveEditor activeEditor = MakeGoodContext.getInstance().getActiveEditor();
        if (!activeEditor.isPHP()) return null;
        return new EditorParser(activeEditor.get());
    }

    public EditorParser(IEditorPart editor) {
        this.editor = editor;
    }

    public ISourceModule getSourceModule() {
        return EditorUtility.getEditorInputModelElement(editor, false);
    }

    public IModelElement getModelElementOnSelection() {
        if (!(editor.getAdapter(Control.class) instanceof StyledText)) return null;
        StyledText text = (StyledText) editor.getAdapter(Control.class);
        if (text == null) return null;

        ISourceModule source = getSourceModule();
        if (source == null) return null;

        IModelElement element = null;
        try {
            element = source.getElementAt(text.getCaretOffset());
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
