package com.piece_framework.makegood.ui.handler;

import com.piece_framework.makegood.ui.launch.MakeGoodLaunchShortcut;

public class RunTestFromEditorInClass extends RunTestFromEditor {
    @Override
    protected int getRunLevel() {
        return MakeGoodLaunchShortcut.RUN_TESTS_ON_CLASS;
    }
}
