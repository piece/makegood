package com.piece_framework.makegood.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.javassist.monitor.WeavingMonitor;
import com.piece_framework.makegood.ui.launch.MakeGoodLaunchShortcut;
import com.piece_framework.makegood.ui.parser.EditorParser;

public class RunTestFromEditor extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        MakeGoodLaunchShortcut shortcut = new MakeGoodLaunchShortcut();
        shortcut.setRunLevelOnEditor(getRunLevel());
        shortcut.launch(editorPart, "run");
        return shortcut;
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

    protected int getRunLevel() {
        return MakeGoodLaunchShortcut.RUN_TEST_ON_CONTEXT;
    }
}
