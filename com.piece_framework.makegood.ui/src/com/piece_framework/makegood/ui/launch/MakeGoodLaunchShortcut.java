package com.piece_framework.makegood.ui.launch;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut;

public class MakeGoodLaunchShortcut extends PHPExeLaunchShortcut {

    @Override
    protected ILaunchConfigurationType getPHPExeLaunchConfigType() {
        ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
        return lm.getLaunchConfigurationType("com.piece_framework.makegood.launch.launchConfigurationType");
    }
}
