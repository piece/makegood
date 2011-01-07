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

package com.piece_framework.makegood.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.aspect.monitor.WeavingMonitor;
import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.ui.launch.TestRunner;

public class RunTestFromExplorer extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        if (selection == null) {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return null;
            IWorkbenchPage page = window.getActivePage();
            if (page == null) return null;
            selection = page.getSelection();
            if (selection == null) return null;
        }

        TestRunner.runTests(selection);
        return null;
    }

    @Override
    public boolean isEnabled() {
        if (!WeavingMonitor.endAll()) return false;
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) return false;
        IWorkbenchPage page = window.getActivePage();
        if (page == null) return false;
        ISelection selection = page.getSelection();
        if (selection == null) return false;

        if (!(selection instanceof IStructuredSelection)) {
            return super.isEnabled();
        }

        Object element = ((IStructuredSelection) selection).getFirstElement();
        IResource resource = null;
        if (element instanceof IResource) {
            resource = (IResource) element;
        } else if (element instanceof IModelElement) {
            resource = ((IModelElement) element).getResource();
        }
        if (resource == null) return false;
        if (resource instanceof IFolder) return true;
        return PHPResource.isPHPSource(resource);
    }
}
