/**
 * Copyright (c) 2011-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
import com.piece_framework.makegood.core.DefaultConfiguration;

/**
 * @since 1.4.0
 */
public class MakeGoodPreference {
    /**
     * @since 2.3.0
     */
    public static final String AUTOTEST_ENABLED = "autotestEnabled"; //$NON-NLS-1$

    public static final String AUTOTEST_SCOPE = "autotestScope"; //$NON-NLS-1$

    /**
     * @since 2.3.0
     */
    private IPreferenceStore preferenceStore;

    /**
     * @since 2.3.0
     */
    public MakeGoodPreference() {
        preferenceStore = Activator.getDefault().getPreferenceStore();
    }

    /**
     * @since 2.3.0
     */
    public void setAutotestEnabled(boolean autotestEnabled) {
        preferenceStore.setValue(AUTOTEST_ENABLED, autotestEnabled);
    }

    /**
     * @since 2.3.0
     */
    public boolean getAutotestEnabled() {
        String autotestScope = preferenceStore.getString(AUTOTEST_SCOPE);
        if (autotestScope.equals(AutotestScope.NONE.name())) {
            return false;
        }

        return preferenceStore.getBoolean(AUTOTEST_ENABLED);
    }

    /**
     * @since 2.3.0
     */
    public void setAutotestScope(AutotestScope autotestScope) {
        preferenceStore.setValue(AUTOTEST_SCOPE, autotestScope.name());
    }

    public AutotestScope getAutotestScope() {
        String autotestScope = preferenceStore.getString(AUTOTEST_SCOPE);
        if (autotestScope.equals(AutotestScope.ALL_TESTS.name())) {
            return AutotestScope.ALL_TESTS;
        } else if (autotestScope.equals(AutotestScope.LAST_TEST.name())) {
            return AutotestScope.LAST_TEST;
        } else if (autotestScope.equals(AutotestScope.FAILED_TESTS.name())) {
            return AutotestScope.FAILED_TESTS;
        } else {
            return new DefaultConfiguration().getAutotestScope();
        }
    }
}
