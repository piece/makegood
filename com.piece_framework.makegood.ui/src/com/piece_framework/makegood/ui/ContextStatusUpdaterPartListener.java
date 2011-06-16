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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.piece_framework.makegood.ui.views.ActivePart;
import com.piece_framework.makegood.ui.views.ResultView;
import com.piece_framework.makegood.ui.views.ViewOpener;

public class ContextStatusUpdaterPartListener implements IPartListener2 {
    private ISelectionChangedListener selectionChangedListener;

    public ContextStatusUpdaterPartListener(ISelectionChangedListener selectionChangedListener) {
        this.selectionChangedListener = selectionChangedListener;
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        IWorkbenchPart activePart = partRef.getPage().getActivePart();
        if (activePart == null) return;
        ActivePart.getInstance().update(activePart);
        if (activePart instanceof AbstractTextEditor) {
            ResultView resultView = (ResultView) ViewOpener.find(ResultView.VIEW_ID);
            if (resultView != null) {
                resultView.updateCommandAvailabilityWithCurrentContext();
            }
        } else {
            ((RunAllTestsSelectionChangedListener) selectionChangedListener).addListener(activePart);
        }
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
}
