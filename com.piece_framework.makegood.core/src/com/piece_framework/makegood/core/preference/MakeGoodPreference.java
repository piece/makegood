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
    public static final String AUTOTEST_SCOPE = "autotestScope"; //$NON-NLS-1$

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
            return new DefaultConfiguration().getAutotestScope();
        }
    }
}
