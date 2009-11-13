package com.piece_framework.makegood.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.piece_framework.makegood.ui.views.TestResultView;

public class NextFailedTestHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        TestResultView view = TestResultView.getView();
        if (view != null) {
            view.nextResult();
        }
        return view;
    }
}
