/**
 * Copyright (c) 2011-2013 KUBO Atsuhiro <kubo@iteman.jp>,
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

import com.piece_framework.makegood.core.Activator;
import com.piece_framework.makegood.core.DefaultConfiguration;
import com.piece_framework.makegood.core.continuoustesting.Scope;

/**
 * @since 1.4.0
 */
public class MakeGoodPreference {
    /**
     * @since 2.3.0
     */
    public static final String CONTINUOUS_TESTING_ENABLED = "autotestEnabled"; //$NON-NLS-1$

    public static final String CONTINUOUS_TESTING_SCOPE = "autotestScope"; //$NON-NLS-1$

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
        preferenceStore.setValue(CONTINUOUS_TESTING_ENABLED, autotestEnabled);
    }

    /**
     * @since 2.3.0
     */
    public boolean getAutotestEnabled() {
        return preferenceStore.getBoolean(CONTINUOUS_TESTING_ENABLED);
    }

    /**
     * @since 2.3.0
     */
    public void setAutotestScope(Scope autotestScope) {
        preferenceStore.setValue(CONTINUOUS_TESTING_SCOPE, autotestScope.name());
    }

    public Scope getAutotestScope() {
        String autotestScope = preferenceStore.getString(CONTINUOUS_TESTING_SCOPE);
        if (autotestScope.equals(Scope.ALL_TESTS.name())) {
            return Scope.ALL_TESTS;
        } else if (autotestScope.equals(Scope.LAST_TEST.name())) {
            return Scope.LAST_TEST;
        } else if (autotestScope.equals(Scope.FAILED_TESTS.name())) {
            return Scope.FAILED_TESTS;
        } else {
            return new DefaultConfiguration().getContinuousTesting().getScope();
        }
    }
}
