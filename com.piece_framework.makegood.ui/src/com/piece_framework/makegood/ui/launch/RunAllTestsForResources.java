/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>
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

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.launch.RuntimeConfiguration;

public class RunAllTestsForResources implements IResourceChangeListener {
    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        if (!RuntimeConfiguration.getInstance().runsAllTestsWhenFileIsSaved) return;
        IResourceDelta[] children = event.getDelta().getAffectedChildren();
        if (children.length == 0) return;

        final ISelection selection = new StructuredSelection(children[0].getResource());
        Job job = new UIJob("MakeGood Run All Tests For Resources") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                if (ActivePart.isAllTestsRunnable(selection)) {
                    TestRunner.runAllTests(selection);
                }

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
}
