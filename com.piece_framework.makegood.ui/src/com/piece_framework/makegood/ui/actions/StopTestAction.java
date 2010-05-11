/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.actions;

import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.piece_framework.makegood.launch.MakeGoodLaunchConfigurationDelegate;
import com.piece_framework.makegood.ui.Activator;

public class StopTestAction implements IViewActionDelegate {
    public static final String ID = "com.piece_framework.makegood.ui.viewActions.resultView.stopTest"; //$NON-NLS-1$
    private static final String MAKEGOOD_IS_STOPPED_BY_ACTION_MARKER = "MAKEGOOD_IS_STOPPED_BY_ACTION_MARKER"; //$NON-NLS-1$

    @Override
    public void init(IViewPart view) {
    }

    @Override
    public void run(IAction action) {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        for (ILaunch launch: manager.getLaunches()) {
            if (!MakeGoodLaunchConfigurationDelegate.isMakeGoodLaunch(launch)) continue;

            launch.setAttribute(MAKEGOOD_IS_STOPPED_BY_ACTION_MARKER, Boolean.TRUE.toString());

            try {
                launch.terminate();
            } catch (DebugException e) {
                Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            }

            break;
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

    public static boolean isStoppedByAction(ILaunch launch) {
        return Boolean.TRUE.toString().equals(launch.getAttribute(MAKEGOOD_IS_STOPPED_BY_ACTION_MARKER));
    }
}
