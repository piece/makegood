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

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.core.result.Result;
import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.ui.Activator;

public class EditorOpener {
    private IEditorReference reusedEditor;

    public static IEditorPart open(IFile file) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) return null;
        IWorkbenchPage page = window.getActivePage();
        if (page == null) return null;
        try {
            return IDE.openEditor(page, file);
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
            return IDE.openEditorOnFileStore(page, fileStore);
        } catch (PartInitException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return null;
        }
    }

    public static IEditorPart open(IFile file, Integer line) {
        IEditorPart editorPart = open(file);
        if (editorPart == null) return null;
        if (line == null) return null;
        gotoLine((ITextEditor) editorPart, line);
        return editorPart;
    }

    public static IEditorPart open(IFileStore fileStore, Integer line) {
        IEditorPart editorPart = open(fileStore);
        if (editorPart == null) return null;
        if (line == null) return null;
        gotoLine((ITextEditor) editorPart, line);
        return editorPart;
    }

    /**
     * @since 1.3.0
     */
    public IEditorPart open(Result result) throws PartInitException {
        String fileName = result.getFile();
        if (fileName == null) return null;
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(fileName));
        if (file != null) {
            if (result instanceof TestCaseResult) {
                if (result.hasFailures() || result.hasErrors()) {
                    return showWithReuse(file, ((TestCaseResult) result).getLine());
                }
                return EditorOpener.open(file, ((TestCaseResult) result).getLine());
            } else {
                return EditorOpener.open(file);
            }
        } else {
            if (result instanceof TestCaseResult) {
                return EditorOpener.open(EFS.getLocalFileSystem().getStore(new Path(fileName)), ((TestCaseResult) result).getLine());
            } else {
                return EditorOpener.open(EFS.getLocalFileSystem().getStore(new Path(fileName)));
            }
        }
    }

    /**
     * @since 1.3.0
     */
    public static IEditorPart open(IMarker marker) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) return null;
        IWorkbenchPage page = window.getActivePage();
        if (page == null) return null;
        try {
            return IDE.openEditor(page, marker);
        } catch (PartInitException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return null;
        }
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

    /**
     * @see org.eclipse.search.internal.ui.text.EditorOpener#showWithReuse()
     * @since 1.3.0
     */
    private IEditorPart showWithReuse(IFile file, Integer line) throws PartInitException {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) return null;
        IWorkbenchPage page = window.getActivePage();
        if (page == null) return null;

        IEditorInput input = new FileEditorInput(file);
        IEditorPart editor = page.findEditor(input);
        if (editor != null) {
            if (line != null) {
                gotoLine((ITextEditor) editor, line);
            }
            page.activate(editor);
            page.bringToTop(editor);
            return editor;
        }

        String editorId = getEditorId(file);
        if (reusedEditor != null && reusedEditor.getEditor(false) != null && !reusedEditor.isDirty() && !reusedEditor.isPinned()) {
            if (!reusedEditor.getId().equals(editorId)) {
                page.closeEditors(new IEditorReference[] { reusedEditor }, false);
                reusedEditor = null;
            } else {
                editor = reusedEditor.getEditor(true);
                if (editor != null && (editor instanceof IReusableEditor)) {
                    ((IReusableEditor) editor).setInput(input);
                    if (line != null) {
                        gotoLine((ITextEditor) editor, line);
                    }
                    page.activate(editor);
                    page.bringToTop(editor);
                    return editor;
                }
            }
        }

        editor = page.openEditor(input, editorId, true);
        if (editor == null) return null;
        if (line != null) {
            gotoLine((ITextEditor) editor, line);
        }
        if (editor instanceof IReusableEditor) {
            reusedEditor = (IEditorReference) page.getReference(editor);
        } else {
            reusedEditor = null;
        }
        return editor;
    }

    /**
     * @see org.eclipse.search.internal.ui.text.EditorOpener#getEditorID()
     * @since 1.3.0
     */
    private String getEditorId(IFile file) throws PartInitException {
        IEditorDescriptor desciptor = IDE.getEditorDescriptor(file);
        if (desciptor == null) {
            return PlatformUI.getWorkbench().getEditorRegistry().findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID).getId();
        }
        return desciptor.getId();
    }
}
