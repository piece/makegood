/**
 * Copyright (c) 2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.ui.views.ResultView;
import com.piece_framework.makegood.ui.views.ViewOpener;

/**
 * @since 1.8.0
 */
public abstract class MoveToFailureAction implements IViewActionDelegate {
    @Override
    public void init(IViewPart view) {
    }

    @Override
    public void run(IAction action) {
        TestLifecycle testLifecycle = TestLifecycle.getInstance();
        if (testLifecycle == null) return;
        if (testLifecycle.getProgress().hasFailures()) {
            ResultView resultView = (ResultView) ViewOpener.open(ResultView.VIEW_ID);
            if (resultView != null) {
                moveToFailure(resultView);
            }
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

    protected abstract void moveToFailure(ResultView resultView);
}
