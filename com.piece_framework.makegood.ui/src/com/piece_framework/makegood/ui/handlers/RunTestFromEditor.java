package com.piece_framework.makegood.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.javassist.monitor.WeavingMonitor;
import com.piece_framework.makegood.ui.launch.EditorParser;
import com.piece_framework.makegood.ui.launch.TestRunner;

public class RunTestFromEditor extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        TestRunner.runTestsInContext(HandlerUtil.getActiveEditor(event));
        return null;
    }

    @Override
    public boolean isEnabled() {
        if (!WeavingMonitor.endAll()) {
            return false;
        }

        EditorParser parser = new EditorParser();
        if (!PHPResource.includeTestClass(parser.getSourceModule())) {
            return false;
        }

        return super.isEnabled();
    }
}
