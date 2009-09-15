package com.piece_framework.stagehand_testrunner;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut;

public class StagehandTestRunnerLaunchShortcut extends PHPExeLaunchShortcut {

    @Override
    protected ILaunchConfigurationType getPHPExeLaunchConfigType() {
        ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
        return lm.getLaunchConfigurationType("com.piece_framework.stagehand_testrunner.launchConfigurationType");
    }
}
