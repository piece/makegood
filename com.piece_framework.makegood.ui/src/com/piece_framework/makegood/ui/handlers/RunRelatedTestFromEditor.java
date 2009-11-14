package com.piece_framework.makegood.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.ui.launch.MakeGoodLaunchShortcut;

public class RunRelatedTestFromEditor extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        MakeGoodLaunchShortcut shortcut = new MakeGoodLaunchShortcut();
        shortcut.setRunLevelOnEditor(MakeGoodLaunchShortcut.RUN_RELATED_TESTS);
        shortcut.launch(editorPart, "run");
        return shortcut;
    }
}
