/**
 * Copyright (c) 2009-2012 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
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
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.piece_framework.makegood.ui.views.ResultViewController;
import com.piece_framework.makegood.ui.views.TestOutlineViewController;

public class MakeGoodPreparer implements IStartup {
    @Override
    public void earlyStartup() {
        prepare();
    }

    /**
     * @since 1.6.0
     */
    private void prepare() {
        MakeGoodContext.getInstance().getStatusMonitor().addPreferenceChangeListener(new InstanceScope());
        TestOutlineViewController testOutlineViewController = new TestOutlineViewController();
        for (IWorkbenchWindow window: PlatformUI.getWorkbench().getWorkbenchWindows()) {
            for (IWorkbenchPage page: window.getPages()) {
                page.addPartListener(MakeGoodContext.getInstance().getStatusMonitor());
                page.addPartListener(testOutlineViewController);

                IWorkbenchPart activePart = page.getActivePart();
                if (activePart != null) {
                    MakeGoodContext.getInstance().getActivePart().update(activePart);
                    if (!(activePart instanceof AbstractTextEditor)) {
                        MakeGoodContext.getInstance().getStatusMonitor().addSelectionChangedListener(activePart);
                    }
                }
            }
        };

        MakeGoodContext.getInstance().addStatusChangeListener(testOutlineViewController);
        DLTKCore.addElementChangedListener(testOutlineViewController);

        DebugPlugin.getDefault().addDebugEventListener(new ResultViewController());
        PlatformUI.getWorkbench().addWorkbenchListener(MakeGoodContext.getInstance());
        ResourcesPlugin.getWorkspace().addResourceChangeListener(new ContinuousTestRunner());
    }
}
