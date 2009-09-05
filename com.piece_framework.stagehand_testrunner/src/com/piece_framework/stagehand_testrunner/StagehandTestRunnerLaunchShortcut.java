package com.piece_framework.stagehand_testrunner;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

public class StagehandTestRunnerLaunchShortcut implements ILaunchShortcut {

    @Override
    public void launch(ISelection selection, String mode) {
        System.out.println(selection);
        System.out.println(mode);
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        System.out.println(editor);
        System.out.println(mode);
    }
}
