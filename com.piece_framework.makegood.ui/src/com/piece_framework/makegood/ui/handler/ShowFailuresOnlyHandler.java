package com.piece_framework.makegood.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

import com.piece_framework.makegood.ui.views.TestResultView;

public class ShowFailuresOnlyHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (!(event.getTrigger() instanceof Event)) {
            return null;
        }
        Event e = (Event) event.getTrigger();

        if (!(e.widget instanceof ToolItem)) {
            return null;
        }
        ToolItem item = (ToolItem) e.widget;

        TestResultView view = TestResultView.getView();
        if (view != null) {
            view.filterResult(item.getSelection());
        }
        return view;
    }
}
