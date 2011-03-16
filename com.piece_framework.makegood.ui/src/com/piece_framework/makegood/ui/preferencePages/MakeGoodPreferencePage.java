/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.preferencePages;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.piece_framework.makegood.core.AutotestScope;
import com.piece_framework.makegood.core.Activator;
import com.piece_framework.makegood.core.preference.MakeGoodPreference;
import com.piece_framework.makegood.ui.Messages;

public class MakeGoodPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    public MakeGoodPreferencePage() {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        FieldEditor fieldEditor = new AutotestRadioGroupFieldEditor(
            MakeGoodPreference.AUTOTEST_SCOPE,
            Messages.MakeGoodPreferencePage_autotestLabel,
            3,
            new String[][] {
                { Messages.MakeGoodPreferencePage_autotestScopeAllTestsLabel, AutotestScope.ALL_TESTS.name() },
                { Messages.MakeGoodPreferencePage_autotestScopeLastTestLabel, AutotestScope.LAST_TEST.name() },
                { Messages.MakeGoodPreferencePage_autotestScopeNoneLabel, AutotestScope.NONE.name() },
            },
            getFieldEditorParent(),
            true
        );
        addField(fieldEditor);
    }

    @Override
    public void init(IWorkbench workbench) {
    }

    private class AutotestRadioGroupFieldEditor extends RadioGroupFieldEditor {
        public AutotestRadioGroupFieldEditor(
            String name,
            String labelText,
            int numColumns,
            String[][] labelAndValues,
            Composite parent,
            boolean useGroup) {
            super(name, labelText, numColumns, labelAndValues, parent, useGroup);
        }

        @Override
        protected void doLoad() {
            MakeGoodPreference.migrate();
            super.doLoad();
        }
    }
}
