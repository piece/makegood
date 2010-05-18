/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import java.io.IOException;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.launch.MakeGoodLaunchConfigurationDelegate;
import com.piece_framework.makegood.launch.RuntimeConfiguration;
import com.piece_framework.makegood.ui.Activator;

public class RunAllTestsForResources implements IDebugEventSetListener, IResourceChangeListener {
    private static final String WAIT_FOR_BUILD = IDebugUIConstants.PLUGIN_ID
                                                 + ".wait_for_build"; //$NON-NLS-1$
    private String oldValueOfWaitForBuild;
    private Boolean oldValueOfOpenDebugViews;
    private Boolean oldValueOfOpenInBrowser;

    @Override
    public void handleDebugEvents(DebugEvent[] events) {
        if (!RuntimeConfiguration.getInstance().runsAllTestsWhenFileIsSaved) return;

        for (DebugEvent event: events) {
            if (event.getKind() != DebugEvent.TERMINATE) continue;
            ILaunch launch = MakeGoodLaunchConfigurationDelegate.getLaunch(event.getSource());
            if (launch == null) continue;
            if (!MakeGoodLaunchConfigurationDelegate.isMakeGoodLaunch(launch)) continue;

            resetProgressMonitor();
            resetDebugView();

            RuntimeConfiguration.getInstance().isRunInBackground = false;

            break;
        }
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        if (!RuntimeConfiguration.getInstance().runsAllTestsWhenFileIsSaved) return;
        if (MakeGoodLaunchConfigurationDelegate.hasActiveMakeGoodLaunches()) return;
        IResourceDelta[] children = event.getDelta().getAffectedChildren();
        if (children.length == 0) return;

        final ISelection selection = new StructuredSelection(children[0].getResource());
        Job job = new UIJob("MakeGood Run All Tests For Resources") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                if (TestRunner.isRunnableAllTests(selection)) {
                    restrictProgressMonitor();
                    restrictDebugView();

                    RuntimeConfiguration.getInstance().isRunInBackground = true;
                    TestRunner.runAllTests(selection);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private void restrictProgressMonitor() {
        ScopedPreferenceStore store = new ScopedPreferenceStore(
                                          new InstanceScope(),
                                          IDebugUIConstants.PLUGIN_ID
                                      );
        String waitForBuild = store.getString(WAIT_FOR_BUILD);
        if (MessageDialogWithToggle.NEVER.equals(waitForBuild)) return;

        store.putValue(WAIT_FOR_BUILD, MessageDialogWithToggle.NEVER);
        savePreference(store);

        oldValueOfWaitForBuild = waitForBuild;
    }

    private void restrictDebugView() {
        ScopedPreferenceStore store = new ScopedPreferenceStore(
                                          new InstanceScope(),
                                          PHPDebugPlugin.getID()
                                      );
        boolean openDebugViews = store.getBoolean(PHPDebugCorePreferenceNames.OPEN_DEBUG_VIEWS);
        boolean openInBrowser = store.getBoolean(PHPDebugCorePreferenceNames.OPEN_IN_BROWSER);
        if (openDebugViews == false && openInBrowser == false) return;

        store.putValue(PHPDebugCorePreferenceNames.OPEN_DEBUG_VIEWS, Boolean.FALSE.toString());
        store.putValue(PHPDebugCorePreferenceNames.OPEN_IN_BROWSER, Boolean.FALSE.toString());
        savePreference(store);

        oldValueOfOpenDebugViews = new Boolean(openDebugViews);
        oldValueOfOpenInBrowser = new Boolean(openInBrowser);
    }

    private void resetProgressMonitor() {
        if (oldValueOfWaitForBuild == null) return;
        ScopedPreferenceStore store = new ScopedPreferenceStore(
                                          new InstanceScope(),
                                          IDebugUIConstants.PLUGIN_ID
                                      );
        boolean changedByUser = !MessageDialogWithToggle.NEVER.equals(
                                    store.getString(WAIT_FOR_BUILD)
                                );
        if (changedByUser) return;

        store.putValue(WAIT_FOR_BUILD, oldValueOfWaitForBuild);
        savePreference(store);

        oldValueOfWaitForBuild = null;
    }

    private void resetDebugView() {
        if (oldValueOfOpenDebugViews == null
            && oldValueOfOpenInBrowser == null
        ) return;
        ScopedPreferenceStore store = new ScopedPreferenceStore(
                                          new InstanceScope(),
                                          PHPDebugPlugin.getID()
                                      );
        boolean changedByUser = store.getBoolean(PHPDebugCorePreferenceNames.OPEN_DEBUG_VIEWS) != false
                                || store.getBoolean(PHPDebugCorePreferenceNames.OPEN_IN_BROWSER) != false;
        if (changedByUser) return;

        store.putValue(PHPDebugCorePreferenceNames.OPEN_DEBUG_VIEWS, oldValueOfOpenDebugViews.toString());
        store.putValue(PHPDebugCorePreferenceNames.OPEN_IN_BROWSER, oldValueOfOpenInBrowser.toString());
        savePreference(store);

        oldValueOfOpenDebugViews = null;
        oldValueOfOpenInBrowser = null;
    }

    private void savePreference(IPersistentPreferenceStore store) {
        try {
            store.save();
        } catch (IOException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
        }
    }
}
