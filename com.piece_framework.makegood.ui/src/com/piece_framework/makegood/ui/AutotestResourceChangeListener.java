/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>
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

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.core.AutotestScope;
import com.piece_framework.makegood.launch.RuntimeConfiguration;
import com.piece_framework.makegood.ui.launch.TestRunner;
import com.piece_framework.makegood.ui.views.ActivePart;

public class AutotestResourceChangeListener implements IResourceChangeListener, IWorkbenchListener {
    private boolean isShuttingDown = false;

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        if (isShuttingDown) return;
        if (!RuntimeConfiguration.getInstance().enablesAutotest()) return;
        IResourceDelta delta = event.getDelta();
        if (delta == null) return;
        IResourceDelta[] deltas = delta.getAffectedChildren();
        if (deltas.length == 0) return;
        if (!shouldRunTests(deltas)) return;

        AutotestScope autotestScope = RuntimeConfiguration.getInstance().getAutotestScope();
        if (autotestScope == AutotestScope.ALL_TESTS) {
            final ISelection selection = new StructuredSelection(deltas[0].getResource());
            if (ActivePart.isAllTestsRunnable(selection)) {
                Job job = new UIJob("MakeGood Run All Tests By Autotest") { //$NON-NLS-1$
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        TestRunner.getInstance().runAllTestsByAutotest(selection);
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            }
        } else if (autotestScope == AutotestScope.LAST_TEST) {
            if (TestRunner.getInstance().hasLastTest()) {
                Job job = new UIJob("MakeGood Run Last Test By Autotest") { //$NON-NLS-1$
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        TestRunner.getInstance().rerunLastTestByAutotest();
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            }
        }
    }

    private boolean shouldRunTests(IResourceDelta[] deltas) {
        for (IResourceDelta delta: deltas) {
            if (delta.getKind() != IResourceDelta.CHANGED) return true;

            int flags = delta.getFlags();
            if ((flags & IResourceDelta.CONTENT) != 0) return true;
            if ((flags & IResourceDelta.REPLACED) != 0) return true;
            if ((flags & IResourceDelta.TYPE) != 0) return true;
            if ((flags & IResourceDelta.LOCAL_CHANGED) != 0) return true;

            return shouldRunTests(delta.getAffectedChildren());
        }

        return false;
    }

    /**
     * @since 1.5.0
     */
    @Override
    public boolean preShutdown(IWorkbench workbench, boolean forced) {
        isShuttingDown = true;
        return true;
    }

    /**
     * @since 1.5.0
     */
    @Override
    public void postShutdown(IWorkbench workbench) {
    }
}
