/**
 * Copyright (c) 2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class MakeGoodStatusMonitor implements IPartListener2, ISelectionChangedListener, IPreferenceChangeListener {
    private static final String[] PREFERENCE_QUALIFIERS = {
        "org.eclipse.dltk.core", //$NON-NLS-1$
        "org.eclipse.php.core", //$NON-NLS-1$
        "org.eclipse.php.debug.core", //$NON-NLS-1$
        "org.eclipse.php.debug.core.Debug_Process_Preferences", //$NON-NLS-1$
        "com.piece_framework.makegood.core", //$NON-NLS-1$
    };

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        IWorkbenchPart activePart = partRef.getPage().getActivePart();
        if (activePart == null) return;
        MakeGoodContext.getInstance().getActivePart().update(activePart);
        if (!(activePart instanceof AbstractTextEditor)) {
            addSelectionChangedListener(activePart);
        }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        MakeGoodContext.getInstance().updateStatus();
    }

    public void addSelectionChangedListener(IWorkbenchPart activePart) {
        ISelectionProvider provider = activePart.getSite().getSelectionProvider();
        if (provider != null) {
            provider.addSelectionChangedListener(this);
        }
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent event) {
        MakeGoodContext.getInstance().updateStatus();
    }

    public void addPreferenceChangeListener(IScopeContext context) {
        for (String qualifier: PREFERENCE_QUALIFIERS) {
            IEclipsePreferences node = context.getNode(qualifier);
            if (node != null) {
                node.addPreferenceChangeListener(this);
            }
        }
    }
}
