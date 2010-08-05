/**
 * Copyright (c) 2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.views;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.ui.Activator;

public class EditorOpen {
    public static IEditorPart open(IFile file) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) return null;
        IWorkbenchPage page = window.getActivePage();
        if (page == null) return null;
        try {
            return org.eclipse.ui.ide.IDE.openEditor(page, file);
        } catch (PartInitException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return null;
        }
    }

    public static IEditorPart open(IFileStore fileStore) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) return null;
        IWorkbenchPage page = window.getActivePage();
        if (page == null) return null;
        try {
            return org.eclipse.ui.ide.IDE.openEditorOnFileStore(page, fileStore);
        } catch (PartInitException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return null;
        }
    }

    public static IEditorPart open(IFile file, Integer line) {
        IEditorPart editorPart = open(file);
        if (editorPart == null) return null;
        gotoLine((ITextEditor) editorPart, line);
        return editorPart;
    }

    public static IEditorPart open(IFileStore fileStore, Integer line) {
        IEditorPart editorPart = open(fileStore);
        if (editorPart == null) return null;
        gotoLine((ITextEditor) editorPart, line);
        return editorPart;
    }

    private static void gotoLine(ITextEditor editor, Integer line) {
        IRegion region;

        try {
            region = editor.getDocumentProvider()
                           .getDocument(editor.getEditorInput())
                           .getLineInformation(line - 1);
        } catch (BadLocationException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return;
        }

        editor.selectAndReveal(region.getOffset(), region.getLength());
    }
}
