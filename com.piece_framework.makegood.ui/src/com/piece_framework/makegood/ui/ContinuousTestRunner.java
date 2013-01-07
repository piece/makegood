/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>
 *               2010-2013 KUBO Atsuhiro <kubo@iteman.jp>,
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.core.continuoustesting.Scope;
import com.piece_framework.makegood.launch.RuntimeConfiguration;

public class ContinuousTestRunner implements IResourceChangeListener {
    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        if (MakeGoodContext.getInstance().isShuttingDown()) return;
        if (!RuntimeConfiguration.getInstance().getContinuousTesting().isEnabled()) return;
        IResourceDelta delta = event.getDelta();
        if (delta == null) return;
        IResourceDelta[] deltas = delta.getAffectedChildren();
        if (deltas.length == 0) return;
        try {
            if (!MakeGoodContext.getInstance().getProjectValidation().validate(deltas[0].getResource().getProject())) return;
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            return;
        }
        if (!shouldRunTests(deltas)) return;

        if (RuntimeConfiguration.getInstance().getContinuousTesting().getScope() == Scope.ALL_TESTS) {
            final ISelection selection = new StructuredSelection(deltas[0].getResource());
            if (ActivePart.isAllTestsRunnable(selection)) {
                Job job = new UIJob("MakeGood Run All Tests By Autotest") { //$NON-NLS-1$
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        MakeGoodContext.getInstance().getTestRunner().runAllTestsByAutotest(selection);
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            }
        } else if (RuntimeConfiguration.getInstance().getContinuousTesting().getScope() == Scope.LAST_TEST) {
            if (MakeGoodContext.getInstance().getTestRunner().hasLastTest()) {
                Job job = new UIJob("MakeGood Run Last Test By Autotest") { //$NON-NLS-1$
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        MakeGoodContext.getInstance().getTestRunner().rerunLastTestByAutotest();
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            }
        } else if (RuntimeConfiguration.getInstance().getContinuousTesting().getScope() == Scope.FAILED_TESTS) {
            if (MakeGoodContext.getInstance().getTestRunner().hasLastTest()) {
                Job job = new UIJob("MakeGood Run Failed Tests By Autotest") { //$NON-NLS-1$
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        MakeGoodContext.getInstance().getTestRunner().rerunFailedTestsByAutotest();
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
}
