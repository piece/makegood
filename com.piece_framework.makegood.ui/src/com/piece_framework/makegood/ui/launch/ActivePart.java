/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.ui.views.OutputView;
import com.piece_framework.makegood.ui.views.ResultView;

public class ActivePart {
    private static ActivePart soleInstance;
    private Object lastTarget;

    private ActivePart() {}

    public static ActivePart getInstance() {
        if (soleInstance == null) {
            soleInstance = new ActivePart();
        }

        return soleInstance;
    }

    public void setPart(IWorkbenchPart part) {
        String id = part.getSite().getId();
        if (id.equals(ResultView.ID)
            || id.equals(OutputView.ID)
            || id.equals("org.eclipse.debug.ui.PHPDebugOutput")) return; //$NON-NLS-1$

        if (part instanceof IEditorPart) {
            lastTarget = part;
        } else {
            ISelectionProvider provider = part.getSite().getSelectionProvider();
            if (provider != null) {
                provider.addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        lastTarget = event.getSelection();
                    }
                });
            }
        }
    }

    static boolean isAllTestsRunnable(Object target) {
        if (target == null) return false;

        IResource resource = getResource(target);
        if (resource == null) return false;
        if (!resource.getProject().exists()) return false;
        if (new MakeGoodProperty(resource).getTestFolders().size() == 0) return false;

        return true;
    }

    public boolean isAllTestsRunnable() {
        return isAllTestsRunnable(lastTarget);
    }

    public Object getLastTarget() {
        return lastTarget;
    }

    static IResource getResource(Object target) {
        if (target instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) target;
            if (selection.getFirstElement() instanceof IModelElement) {
                return ((IModelElement) selection.getFirstElement()).getResource();
            } else if (selection.getFirstElement() instanceof IResource) {
                return (IResource) selection.getFirstElement();
            }
        } else if (target instanceof IEditorPart) {
            ISourceModule source =
                EditorUtility.getEditorInputModelElement((IEditorPart) target, false);
            if (source != null) {
                return source.getResource();
            }

            IEditorPart editor = (IEditorPart) target;
            if (editor.getEditorInput() instanceof IFileEditorInput) {
                return ((IFileEditorInput) editor.getEditorInput()).getFile();
            }
        }

        return null;
    }
}
