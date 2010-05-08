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

package com.piece_framework.makegood.ui.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.php.internal.debug.core.model.IPHPDebugTarget;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.ui.ide.ViewShow;

public class OutputDebugEventSetListener implements IDebugEventSetListener {
    @Override
    public void handleDebugEvents(DebugEvent[] events) {
        if (events == null) return;
        int size = events.length;
        for (int i = 0; i < size; ++i) {
            final Object source = events[i].getSource();
            if (!(source instanceof IPHPDebugTarget || source instanceof IProcess)) continue;
            if (events[i].getKind() == DebugEvent.CREATE) {
                Job job = new UIJob("MakeGood Output") { //$NON-NLS-1$
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        OutputView outputView = (OutputView) ViewShow.find(OutputView.ID);
                        if (outputView == null) return Status.CANCEL_STATUS;
                        outputView.setText("");

                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            } else if (events[i].getKind() == DebugEvent.TERMINATE) {
                Job job = new UIJob("MakeGood Output") { //$NON-NLS-1$
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        String text = null;
                        if (source instanceof IPHPDebugTarget) {
                            text = ((IPHPDebugTarget) source).getOutputBuffer().toString();
                        } else if (source instanceof IProcess) {
                            text = ((IProcess) source).getStreamsProxy().getOutputStreamMonitor().getContents();
                        }

                        OutputView outputView = (OutputView) ViewShow.find(OutputView.ID);
                        if (outputView == null) return Status.CANCEL_STATUS;
                        outputView.setText(text);

                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            }
        }
    }
}
