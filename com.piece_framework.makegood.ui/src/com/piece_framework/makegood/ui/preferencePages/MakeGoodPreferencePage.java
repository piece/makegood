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

package com.piece_framework.makegood.ui.preferencePages;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.piece_framework.makegood.core.MakeGoodCorePlugin;
import com.piece_framework.makegood.core.preference.MakeGoodPreferenceInitializer;
import com.piece_framework.makegood.ui.Messages;

public class MakeGoodPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private Button runAllTestsAutomatically;

    public MakeGoodPreferencePage() {}

    public MakeGoodPreferencePage(String title) {
        super(title);
    }

    public MakeGoodPreferencePage(String title, ImageDescriptor image) {
        super(title, image);
    }

    @Override
    public void init(IWorkbench workbench) {}

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        runAllTestsAutomatically = new Button(composite, SWT.CHECK);
        runAllTestsAutomatically.setLayoutData(
            new GridData(GridData.VERTICAL_ALIGN_BEGINNING)
        );
        runAllTestsAutomatically.setText(
            Messages.MakeGoodPreferencePage_runAllTestsAutomatically
        );

        IPreferenceStore store = MakeGoodCorePlugin.getDefault().getPreferenceStore();
        runAllTestsAutomatically.setSelection(
            store.getBoolean(MakeGoodPreferenceInitializer.RUN_ALL_TESTS_AUTOMATICALLY)
        );

        return composite;
    }

    @Override
    public boolean performOk() {
        IPreferenceStore store = MakeGoodCorePlugin.getDefault().getPreferenceStore();
        store.setValue(
            MakeGoodPreferenceInitializer.RUN_ALL_TESTS_AUTOMATICALLY,
            runAllTestsAutomatically.getSelection()
        );
        return true;
    }

    @Override
    protected void performDefaults() {
        IPreferenceStore store = MakeGoodCorePlugin.getDefault().getPreferenceStore();
        runAllTestsAutomatically.setSelection(
            store.getDefaultBoolean(MakeGoodPreferenceInitializer.RUN_ALL_TESTS_AUTOMATICALLY)
        );
    }
}
