package com.piece_framework.makegood.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.piece_framework.makegood.ui.launch.TestRunner;

public class RerunTestAction implements IViewActionDelegate {
    public static final String ID = "com.piece_framework.makegood.ui.viewActions.resultView.rerunTest"; //$NON-NLS-1$

    @Override
    public void init(IViewPart view) {
    }

    @Override
    public void run(IAction action) {
        if (TestRunner.hasLastTest()) {
            TestRunner.rerunLastTest();
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }
}
