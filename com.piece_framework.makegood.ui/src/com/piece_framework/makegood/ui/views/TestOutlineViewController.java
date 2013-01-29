/**
 * Copyright (c) 2012-2013 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
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
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.ui.ActiveEditor;
import com.piece_framework.makegood.ui.MakeGoodContext;
import com.piece_framework.makegood.ui.MakeGoodStatus;
import com.piece_framework.makegood.ui.MakeGoodStatusChangeListener;

/**
 * @since 2.3.0
 */
public class TestOutlineViewController implements IPartListener2, MakeGoodStatusChangeListener, IElementChangedListener, CaretListener {
    private int currentEditorCode = 0;

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        if (partRef.hashCode() == currentEditorCode) return;
        currentEditorCode = partRef.hashCode();
        if (!(partRef.getPart(false) instanceof IEditorPart)) return;

        updateTestOutline();

        ActiveEditor activeEditor = MakeGoodContext.getInstance().getActiveEditor();
        if (!activeEditor.isPHP()) return;
        StyledText text = (StyledText) activeEditor.get().getAdapter(Control.class);
        text.addCaretListener(this);
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
        if (view != null) view.refresh();
    }

    @Override
    public void caretMoved(CaretEvent event) {
        TestOutlineView view = (TestOutlineView) ViewOpener.find(TestOutlineView.ID);
        if (view != null) view.selectCurrentElement();
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
