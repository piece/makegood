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

import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.php.internal.debug.core.zend.model.PHPDebugTarget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.swt.LinkedText;

@SuppressWarnings("restriction")
public class OutputView extends ViewPart {
    public static final String ID = Activator.PLUGIN_ID + ".views.outputView"; //$NON-NLS-1$
    private LinkedText output;
    private IDebugEventSetListener terminateListener;

    @Override
    public void createPartControl(Composite parent) {
        output =
            new LinkedText(
                parent,
                Pattern.compile("in (.+) on line (\\d+)", Pattern.MULTILINE) //$NON-NLS-1$
            );
        terminateListener = new IDebugEventSetListener() {
            @Override
            public void handleDebugEvents(DebugEvent[] events) {
                if (events == null) {
                    return;
                }

                int size = events.length;
                for (int i = 0; i < size; ++i) {
                    Object obj = events[i].getSource();
                    if (!(obj instanceof PHPDebugTarget)) continue;
                    if (events[i].getKind() != DebugEvent.TERMINATE) continue;
                    final PHPDebugTarget target = (PHPDebugTarget)obj;
                    Job job = new UIJob("MakeGood Output") { //$NON-NLS-1$
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            setText(target.getOutputBuffer().toString());
                            return Status.OK_STATUS;
                        }
                    };
                    job.schedule();
                }
            }
        };
        DebugPlugin.getDefault().addDebugEventListener(terminateListener);
    }

    @Override
    public void setFocus() {}

    public void setText(String text) {
        output.setText(text);
    }

    @Override
    public void dispose() {
        DebugPlugin.getDefault().removeDebugEventListener(terminateListener);
    }
}
