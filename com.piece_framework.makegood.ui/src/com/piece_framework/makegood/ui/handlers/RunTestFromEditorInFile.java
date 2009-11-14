package com.piece_framework.makegood.ui.handlers;

import com.piece_framework.makegood.ui.launch.MakeGoodLaunchShortcut;

public class RunTestFromEditorInFile extends RunTestFromEditor {
    @Override
    protected int getRunLevel() {
        return MakeGoodLaunchShortcut.RUN_TESTS_ON_FILE;
    }
}
