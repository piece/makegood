package com.piece_framework.makegood.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.piece_framework.makegood.ui.Activator;

public class TerminateTestAction implements IViewActionDelegate {
    @Override
    public void init(IViewPart view) {
    }

    @Override
    public void run(IAction action) {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        for (ILaunch launch: manager.getLaunches()) {
            boolean isMakeGood = launch.getLaunchConfiguration().getName().startsWith("MakeGood"); //$NON-NLS-1$
            if (isMakeGood) {
                try {
                    launch.terminate();
                } catch (DebugException e) {
                    Activator.getDefault().getLog().log(
                        new Status(
                            Status.ERROR,
                            Activator.PLUGIN_ID,
                            e.getMessage(),
                            e
                        )
                    );
                }
                break;
            }
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }
}
