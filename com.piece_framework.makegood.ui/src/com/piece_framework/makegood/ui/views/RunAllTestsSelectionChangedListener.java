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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.piece_framework.makegood.ui.ide.ViewShow;
import com.piece_framework.makegood.ui.launch.ActivePart;

public class RunAllTestsSelectionChangedListener implements ISelectionChangedListener {
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        ResultView resultView = (ResultView) ViewShow.find(ResultView.ID);
        if (resultView != null) {
            resultView.updateStateOfRunAllTestsAction();
        }
    }

    public void addListener(IWorkbenchPage page) {
        IWorkbenchPart activePart = page.getActivePart();
        if (activePart == null) return;

        if (!(activePart instanceof AbstractTextEditor)) {
            ISelectionProvider provider = activePart.getSite().getSelectionProvider();
            if (provider != null) {
                provider.addSelectionChangedListener(this);
            }
        }

        ActivePart.getInstance().setPart(activePart);
    }
}
