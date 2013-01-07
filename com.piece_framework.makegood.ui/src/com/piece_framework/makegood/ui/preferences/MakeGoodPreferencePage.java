/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2011-2013 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.piece_framework.makegood.core.Activator;
import com.piece_framework.makegood.core.DefaultConfiguration;
import com.piece_framework.makegood.core.continuoustesting.ContinuousTesting;
import com.piece_framework.makegood.core.continuoustesting.Scope;
import com.piece_framework.makegood.core.preference.MakeGoodPreference;
import com.piece_framework.makegood.ui.Messages;

public class MakeGoodPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    /**
     * @since 2.3.0
     */
    private Button continuousTestingEnabledButton;

    /**
     * @since 2.3.0
     */
    private Button continuousTestingScopeAllTestsButton;

    /**
     * @since 2.3.0
     */
    private Button continuousTestingScopeLastTestButton;

    /**
     * @since 2.3.0
     */
    private Button continuousTestingFailedTestsButton;

    @Override
    public void init(IWorkbench workbench) {
    }

    /**
     * @since 2.3.0
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite contents = createComposite(parent);
        createContinuousTestingGroup(contents);

        return contents;
    }

    /**
     * @since 2.3.0
     */
    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return Activator.getDefault().getPreferenceStore();
    }

    /**
     * @since 2.3.0
     */
    @Override
    protected void performDefaults() {
        ContinuousTesting continuousTesting = new DefaultConfiguration().getContinuousTesting();

        continuousTestingEnabledButton.setSelection(continuousTesting.isEnabled());

        continuousTestingScopeAllTestsButton.setSelection(continuousTesting.getScope() == Scope.ALL_TESTS);
        continuousTestingScopeLastTestButton.setSelection(continuousTesting.getScope() == Scope.LAST_TEST);
        continuousTestingFailedTestsButton.setSelection(continuousTesting.getScope() == Scope.FAILED_TESTS);

        super.performDefaults();
    }

    /**
     * @since 2.3.0
     */
    @Override
    public boolean performOk() {
        MakeGoodPreference preference = new MakeGoodPreference();

        preference.setContinuousTestingEnabled(continuousTestingEnabledButton.getSelection());

        if (continuousTestingScopeAllTestsButton.getSelection()) {
            preference.setContinuousTestingScope(Scope.ALL_TESTS);
        } else if (continuousTestingScopeLastTestButton.getSelection()) {
            preference.setContinuousTestingScope(Scope.LAST_TEST);
        } else if (continuousTestingFailedTestsButton.getSelection()) {
            preference.setContinuousTestingScope(Scope.FAILED_TESTS);
        }

        return true;
    }

    /**
     * @since 2.3.0
     */
    private Composite createComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

        return composite;
    }

    /**
     * @since 2.3.0
     */
    private Group createContinuousTestingGroup(Composite parent) {
        Group autotestGroup = new Group(parent, SWT.LEFT);
        autotestGroup.setLayout(new GridLayout());
        autotestGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        autotestGroup.setText(Messages.MakeGoodPreferencePage_autotestGroupLabel);

        continuousTestingEnabledButton = createContinuousTestingEnabledButton(autotestGroup);

        continuousTestingScopeAllTestsButton = createContinuousTestingScopeAlltestsButton(autotestGroup);
        continuousTestingScopeAllTestsButton.setLayoutData(createIndentedLayoutData());
        continuousTestingScopeLastTestButton = createContinuousTestingScopeLastTestButton(autotestGroup);
        continuousTestingScopeLastTestButton.setLayoutData(createIndentedLayoutData());
        continuousTestingFailedTestsButton = createAutotestScopeFailedTestsButton(autotestGroup);
        continuousTestingFailedTestsButton.setLayoutData(createIndentedLayoutData());

        return autotestGroup;
    }

    /**
     * @since 2.3.0
     */
    private Button createContinuousTestingEnabledButton(Composite parent) {
        Button button = new Button(parent, SWT.CHECK);
        button.setText(Messages.MakeGoodPreferencePage_autotestLabel);
        button.setSelection(new MakeGoodPreference().getContinuousTestingEnabled());

        return button;
    }

    /**
     * @since 2.3.0
     */
    private Button createContinuousTestingScopeAlltestsButton(Composite parent) {
        Button button = new Button(parent, SWT.RADIO);
        button.setText(Messages.MakeGoodPreferencePage_autotestScopeAllTestsLabel);
        button.setSelection(new MakeGoodPreference().getContinuousTestingScope() == Scope.ALL_TESTS);

        return button;
    }

    /**
     * @since 2.3.0
     */
    private Button createContinuousTestingScopeLastTestButton(Composite parent) {
        Button button = new Button(parent, SWT.RADIO);
        button.setText(Messages.MakeGoodPreferencePage_autotestScopeLastTestLabel);
        button.setSelection(new MakeGoodPreference().getContinuousTestingScope() == Scope.LAST_TEST);

        return button;
    }

    /**
     * @since 2.3.0
     */
    private Button createAutotestScopeFailedTestsButton(Composite parent) {
        Button button = new Button(parent, SWT.RADIO);
        button.setText(Messages.MakeGoodPreferencePage_autotestScopeFailedTestsLabel);
        button.setSelection(new MakeGoodPreference().getContinuousTestingScope() == Scope.FAILED_TESTS);

        return button;
    }

    /**
     * @since 2.3.0
     */
    private GridData createIndentedLayoutData() {
        GridData layoutData = new GridData();
        layoutData.horizontalIndent = 20;

        return layoutData;
    }
}
