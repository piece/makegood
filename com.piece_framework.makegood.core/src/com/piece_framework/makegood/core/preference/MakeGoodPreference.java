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

package com.piece_framework.makegood.core.preference;

import org.eclipse.jface.preference.IPreferenceStore;

import com.piece_framework.makegood.core.AutotestScope;
import com.piece_framework.makegood.core.Activator;

/**
 * @since 1.4.0
 */
public class MakeGoodPreference {
    /**
     * @deprecated
     */
    public static final String RUN_ALL_TESTS_WHEN_FILE_IS_SAVED = "RUN_ALL_TESTS_WHEN_FILE_IS_SAVED"; //$NON-NLS-1$

    public static final String AUTOTEST_SCOPE = "autotestScope"; //$NON-NLS-1$

    public static void migrate() {
        IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
        if (!preferenceStore.contains(RUN_ALL_TESTS_WHEN_FILE_IS_SAVED)) return;

        if (preferenceStore.getBoolean(RUN_ALL_TESTS_WHEN_FILE_IS_SAVED)) {
            preferenceStore.setValue(AUTOTEST_SCOPE, AutotestScope.ALL_TESTS.name());
        } else {
            preferenceStore.setValue(AUTOTEST_SCOPE, AutotestScope.NONE.name());
        }

        removePreference(RUN_ALL_TESTS_WHEN_FILE_IS_SAVED);
    }

    public static AutotestScope getAutotestScope() {
        IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
        String autotestScope = preferenceStore.getString(AUTOTEST_SCOPE);
        if (autotestScope.equals(AutotestScope.ALL_TESTS.name())) {
            return AutotestScope.ALL_TESTS;
        } else if (autotestScope.equals(AutotestScope.LAST_TEST.name())) {
            return AutotestScope.LAST_TEST;
        } else if (autotestScope.equals(AutotestScope.NONE.name())) {
            return AutotestScope.NONE;
        } else {
            return AutotestScope.ALL_TESTS;
        }
    }

    private static void removePreference(String name) {
        IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
        preferenceStore.setDefault(name, true);
        preferenceStore.setToDefault(name);
    }
}
