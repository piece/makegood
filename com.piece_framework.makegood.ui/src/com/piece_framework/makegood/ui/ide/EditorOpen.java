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

package com.piece_framework.makegood.ui.ide;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.swt.ExternalFileWithLineRange;
import com.piece_framework.makegood.ui.swt.FileWithLineRange;
import com.piece_framework.makegood.ui.swt.InternalFileWithLineRange;

public class EditorOpen {
    public static IEditorPart open(IFile file) {
        try {
            return org.eclipse.ui.ide.IDE.openEditor(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
                        file
                    );
        } catch (PartInitException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return null;
        }
    }

    public static IEditorPart open(IFileStore fileStore) {
        try {
            return org.eclipse.ui.ide.IDE.openEditorOnFileStore(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
                        fileStore
                    );
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
