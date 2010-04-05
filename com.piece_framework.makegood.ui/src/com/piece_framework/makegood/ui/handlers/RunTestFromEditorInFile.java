package com.piece_framework.makegood.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.ui.launch.TestRunner;

public class RunTestFromEditorInFile extends RunTestFromEditor {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        TestRunner.runTestsInFile(HandlerUtil.getActiveEditor(event));
        return null;
    }
}
