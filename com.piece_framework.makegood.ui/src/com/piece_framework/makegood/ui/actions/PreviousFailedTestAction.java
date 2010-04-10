package com.piece_framework.makegood.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.piece_framework.makegood.ui.ide.ViewShow;
import com.piece_framework.makegood.ui.views.ResultView;

public class PreviousFailedTestAction implements IViewActionDelegate {
    @Override
    public void init(IViewPart view) {
    }

    @Override
    public void run(IAction action) {
        ResultView view = (ResultView)ViewShow.find(ResultView.ID);
        if (view != null) {
            view.previousResult();
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }
}
