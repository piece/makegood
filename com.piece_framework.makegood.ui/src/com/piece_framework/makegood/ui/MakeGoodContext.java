/**
 * Copyright (c) 2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.ui.launch.TestRunner;

/**
 * @since 1.6.0
 */
public class MakeGoodContext implements IWorkbenchListener {
    private static MakeGoodContext soleInstance;
    private boolean isShuttingDown = false;
    private ActivePart activePart = new ActivePart();
    private ActiveEditor activeEditor = new ActiveEditor();
    private ProjectValidation projectValidation = new ProjectValidation();
    private TestRunner testRunner = new TestRunner();
    private MakeGoodStatusMonitor statusMonitor = new MakeGoodStatusMonitor();
    private MakeGoodStatus status;
    private List<MakeGoodStatusChangeListener> statusChangeListeners = new ArrayList<MakeGoodStatusChangeListener>();

    private MakeGoodContext() {
    }

    public static MakeGoodContext getInstance() {
        if (soleInstance == null) {
            soleInstance = new MakeGoodContext();
        }
        return soleInstance;
    }

    @Override
    public boolean preShutdown(IWorkbench workbench, boolean forced) {
        isShuttingDown = true;
        return true;
    }

    @Override
    public void postShutdown(IWorkbench workbench) {
    }

    public boolean isShuttingDown() {
        return isShuttingDown;
    }

    public ActivePart getActivePart() {
        return activePart;
    }

    /**
     * @since 1.x.0
     */
    public ActiveEditor getActiveEditor() {
        return activeEditor;
    }

    public ProjectValidation getProjectValidation() {
        return projectValidation;
    }

    public TestRunner getTestRunner() {
        return testRunner;
    }

    public MakeGoodStatusMonitor getStatusMonitor() {
        return statusMonitor;
    }

    public void updateStatus() {
        if (TestLifecycle.isRunning()) return;
        try {
            if (projectValidation.validate(activePart.getProject())) {
                updateStatus(MakeGoodStatus.WaitingForTestRun, activePart.getProject());
            }
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            return;
        }
    }

    public void updateStatus(MakeGoodStatus status) {
        this.status = status;
        for (MakeGoodStatusChangeListener listener: statusChangeListeners) {
            listener.statusChanged(this.status);
        }
    }

    public void updateStatus(MakeGoodStatus status, IProject project) {
        status.setProject(project);
        updateStatus(status);
    }

    public void addStatusChangeListener(MakeGoodStatusChangeListener listener) {
        if (!statusChangeListeners.contains(listener)) {
            statusChangeListeners.add(listener);
        }
    }

    public void removeStatusChangeListener(MakeGoodStatusChangeListener listener) {
        statusChangeListeners.remove(listener);
    }
}
