package com.piece_framework.makegood.ui.launch;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut;
import org.eclipse.ui.IEditorPart;

public class MakeGoodLaunchShortcut extends PHPExeLaunchShortcut {
    @Override
    public void launch(final ISelection selection, final String mode) {
        super.launch(selection, mode);
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        super.launch(editor, mode);
    }

    @Override
    protected ILaunchConfigurationType getPHPExeLaunchConfigType() {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        return manager.getLaunchConfigurationType("com.piece_framework.makegood.launch.launchConfigurationTypes.makeGood"); //$NON-NLS-1$
    }
}
