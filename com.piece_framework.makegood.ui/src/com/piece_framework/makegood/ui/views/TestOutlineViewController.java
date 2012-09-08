/**
 * Copyright (c) 2012 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.ui.MakeGoodStatus;
import com.piece_framework.makegood.ui.MakeGoodStatusChangeListener;

/**
 * @since 1.x.0
 */
public class TestOutlineViewController implements IPartListener2, MakeGoodStatusChangeListener, IElementChangedListener {
    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        if (partRef.getId().equals(TestOutlineView.ID)) return;
        updateTestOutline();
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
    }

    @Override
    public void elementChanged(ElementChangedEvent event) {
        updateTestOutline();
    }

    @Override
    public void statusChanged(MakeGoodStatus status) {
        TestOutlineView view = (TestOutlineView) ViewOpener.find(TestOutlineView.ID);
        if (view == null) return;

        if (status == MakeGoodStatus.RunningTest) {
            view.setRunningTest(true);
        } else if (status == MakeGoodStatus.WaitingForTestRun && view.runningTest()) {
            updateTestOutline();
            view.setRunningTest(false);
        }
    }

    private void updateTestOutline() {
        Job job = new UIJob("MakeGood Test Outline View Updated") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                TestOutlineView view = (TestOutlineView) ViewOpener.find(TestOutlineView.ID);
                if (view != null) {
                    view.updateTestOutline();
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
}
