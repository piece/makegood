/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.views;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.piece_framework.makegood.ui.launch.ActivePart;

public class RunAllTestsPartListener implements IPartListener2 {
    private ISelectionChangedListener selectionChangedListener;

    public RunAllTestsPartListener(ISelectionChangedListener selectionChangedListener) {
        this.selectionChangedListener = selectionChangedListener;
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        IWorkbenchPart activePart = partRef.getPage().getActivePart();
        if (activePart == null) return;
        ActivePart.getInstance().setPart(activePart);
        if (!(activePart instanceof AbstractTextEditor)) {
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
