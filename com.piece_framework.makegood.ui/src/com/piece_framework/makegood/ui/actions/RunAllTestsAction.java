package com.piece_framework.makegood.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.piece_framework.makegood.ui.launch.TestRunner;

public class RunAllTestsAction implements IViewActionDelegate {
    public static final String ID = "com.piece_framework.makegood.ui.viewActions.resultView.runAllTests"; //$NON-NLS-1$

    @Override
    public void init(IViewPart view) {
    }

    @Override
    public void run(IAction action) {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (activePage.getActiveEditor() != null) {
            TestRunner.runAllTests(activePage.getActiveEditor());
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }
}
