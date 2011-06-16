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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.piece_framework.makegood.ui.views.ResultViewController;

public class ListenerPreparer implements IStartup {
    @Override
    public void earlyStartup() {
        ISelectionChangedListener selectionChangedListener = new ContextStatusUpdaterSelectionChangedListener();
        IPartListener2 partListener = new ContextStatusUpdaterPartListener(selectionChangedListener);
        for (IWorkbenchWindow window: PlatformUI.getWorkbench().getWorkbenchWindows()) {
            for (IWorkbenchPage page: window.getPages()) {
                page.addPartListener(partListener);
                IWorkbenchPart activePart = page.getActivePart();
                if (activePart != null && !(activePart instanceof AbstractTextEditor)) {
                    ((ContextStatusUpdaterSelectionChangedListener) selectionChangedListener).addListener(activePart);
                }
            }
        };

        DebugPlugin.getDefault().addDebugEventListener(new ResultViewController());
        AutotestResourceChangeListener autotestResourceChangeListener = new AutotestResourceChangeListener();
        PlatformUI.getWorkbench().addWorkbenchListener(autotestResourceChangeListener);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(autotestResourceChangeListener);
    }
}
