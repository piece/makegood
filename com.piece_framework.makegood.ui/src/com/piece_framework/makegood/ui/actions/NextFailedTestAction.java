package com.piece_framework.makegood.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.piece_framework.makegood.ui.views.TestResultView;

public class NextFailedTestAction implements IViewActionDelegate {
    @Override
    public void init(IViewPart view) {
    }

    @Override
    public void run(IAction action) {
        TestResultView view = TestResultView.getView();
        if (view != null) {
            view.nextResult();
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }
}
