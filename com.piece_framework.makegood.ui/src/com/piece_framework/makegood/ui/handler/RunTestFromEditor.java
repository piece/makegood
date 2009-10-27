package com.piece_framework.makegood.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.ui.launch.MakeGoodLaunchShortcut;

public class RunTestFromEditor extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
        MakeGoodLaunchShortcut shortcut = new MakeGoodLaunchShortcut();
        shortcut.launch(editorPart, "run");
        return shortcut;
    }
}
