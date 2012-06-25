/**
 * Copyright (c) 2012 KUBO Atsuhiro <kubo@iteman.jp>,
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

import com.piece_framework.makegood.ui.MakeGoodContext;

public class RerunFailedTestsAction implements IViewActionDelegate {
    public static final String ACTION_ID = "com.piece_framework.makegood.ui.viewActions.rerunFailedTestsAction"; //$NON-NLS-1$

    @Override
    public void init(IViewPart view) {
    }

    @Override
    public void run(IAction action) {
        if (MakeGoodContext.getInstance().getTestRunner().hasLastTest()) {
            MakeGoodContext.getInstance().getTestRunner().rerunFailedTests();
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }
}
