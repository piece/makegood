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

package com.piece_framework.makegood.core.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.piece_framework.makegood.core.Activator;
import com.piece_framework.makegood.core.DefaultConfiguration;

public class MakeGoodPreferenceInitializer extends AbstractPreferenceInitializer {
    @Override
    public void initializeDefaultPreferences() {
        Activator.getDefault().getPreferenceStore().setDefault(MakeGoodPreference.CONTINUOUS_TESTING_ENABLED, new DefaultConfiguration().getAutotestEnabled());
        Activator.getDefault().getPreferenceStore().setDefault(MakeGoodPreference.CONTINUOUS_TESTING_SCOPE, new DefaultConfiguration().getAutotestScope().name());
    }
}
