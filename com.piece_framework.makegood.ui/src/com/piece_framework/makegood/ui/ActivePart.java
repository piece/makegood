/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;

import com.piece_framework.makegood.core.preference.MakeGoodProperty;
import com.piece_framework.makegood.ui.views.ResultView;

public class ActivePart {
    /**
     * @since 1.6.0
     */
    private static final String VIEW_ID_PHPDEBUGOUTPUT = "org.eclipse.debug.ui.PHPDebugOutput"; //$NON-NLS-1$

    /**
     * @since 1.6.0
     */
    private static final String VIEW_ID_PHPBROWSEROUTPUT = "org.eclipse.debug.ui.PHPBrowserOutput"; //$NON-NLS-1$

    private Object entity;

    public void update(IWorkbenchPart part) {
        String id = part.getSite().getId();
        if (ResultView.VIEW_ID.equals(id)) return;
        if (VIEW_ID_PHPDEBUGOUTPUT.equals(id)) return;
        if (VIEW_ID_PHPBROWSEROUTPUT.equals(id)) return;
        if (IConsoleConstants.ID_CONSOLE_VIEW.equals(id)) return;

        if (shouldUpdateLink(part)) {
            updateLink(part);
        }

        if (!(part instanceof IEditorPart)) {
            ISelectionProvider provider = part.getSite().getSelectionProvider();
            if (provider != null) {
                provider.addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        if (shouldUpdateLink(event.getSelection())) {
                            updateLink(event.getSelection());
                        }
                    }
                });
            }
        }
    }

    public void update() {
        IWorkbenchPart activePart = getActivePart();
        if (activePart != null) {
            update(activePart);
        }
    }

    public static boolean isAllTestsRunnable(Object target) {
        if (target == null) return false;

        IResource resource = getResource(target);
        if (resource == null) return false;
        if (!resource.getProject().exists()) return false;
        if (new MakeGoodProperty(resource).getTestFolders().size() == 0) return false;

        return true;
    }

    public boolean isAllTestsRunnable() {
        return isAllTestsRunnable(entity);
    }

    public Object getEntity() {
        return entity;
    }

    public static IResource getResource(Object target) {
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

    public IProject getProject() {
        return getProject(entity);
    }

    /**
     * @since 1.6.0
     */
    public static IWorkbenchPart getActivePart() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) return null;
        IWorkbenchPage page = window.getActivePage();
        if (page == null) return null;
        return page.getActivePart();
    }

    /**
     * @since 1.5.0
     */
    private IProject getProject(Object target) {
        if (target == null) return null;
        IResource resource = getResource(target);
        if (resource == null) return null;
        return resource.getProject();
    }

    /**
     * @since 1.5.0
     */
    private boolean shouldUpdateLink(Object target) {
        IProject project = getProject(target);
        if (project == null) return false;
        if (!project.exists()) return false;
        return true;
    }

    /**
     * @since 1.6.0
     */
    private void updateLink(Object entity) {
        this.entity = entity;

        IProject project = getProject(this.entity);
        if (project != null) {
            MakeGoodContext.getInstance().getStatusMonitor().addPreferenceChangeListener(new ProjectScope(project));
        }

        MakeGoodContext.getInstance().updateStatus();
    }
}
