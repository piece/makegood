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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.piece_framework.makegood.ui.Activator;

public class ViewShow {
    public static IViewPart show(String viewId) {
        IWorkbenchPage page = getActivePage();
        if (page == null) return null;
        try {
            return page.showView(viewId);
        } catch (PartInitException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return null;
        }
    }

    public static void setFocus(IWorkbenchPart part) {
        IWorkbenchPage page = getActivePage();
        if (page == null) return;
        page.activate(part);
    }

    public static IWorkbenchPart getActivePart() {
        IWorkbenchPage page = getActivePage();
        if (page == null) return null;
        return page.getActivePart();
    }

    public static IViewPart find(String viewId) {
        IWorkbenchPage page = getActivePage();
        if (page == null) return null;
        return page.findView(viewId);
    }

    private static IWorkbenchPage getActivePage() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) return null;
        return window.getActivePage();
    }
}
