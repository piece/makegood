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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.piece_framework.makegood.ui.views.OutputView;
import com.piece_framework.makegood.ui.views.ResultView;

public class AllTestsStatus implements IPartListener {
    private static AllTestsStatus status;
    private Object target;

    private AllTestsStatus() {
        for (IWorkbenchWindow window: PlatformUI.getWorkbench().getWorkbenchWindows()) {
            for (IWorkbenchPage page: window.getPages()) {
                page.addPartListener(this);

                partActivated(page.getActivePart());
            }
        }
    }

    public static AllTestsStatus getInstance() {
        if (status == null) {
            status = new AllTestsStatus();
        }
        return status;
    }

    @Override
    public void partActivated(IWorkbenchPart part) {
        String id = part.getSite().getId();
        if (id.equals(ResultView.ID)
            || id.equals(OutputView.ID)
            || id.equals("org.eclipse.debug.ui.PHPDebugOutput")) return; //$NON-NLS-1$

        if (part instanceof IEditorPart) {
            target = part;
        } else {
            ISelectionProvider provider = part.getSite().getSelectionProvider();
            if (provider != null) {
                provider.addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        target = event.getSelection();
                    }
                });
            }
        }
    }

    public boolean runnable() {
        return TestRunner.isRunnableAllTests(target);
    }

    public Object getTarget() {
        return target;
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {}

    @Override
    public void partClosed(IWorkbenchPart part) {}

    @Override
    public void partDeactivated(IWorkbenchPart part) {}

    @Override
    public void partOpened(IWorkbenchPart part) {}
}
