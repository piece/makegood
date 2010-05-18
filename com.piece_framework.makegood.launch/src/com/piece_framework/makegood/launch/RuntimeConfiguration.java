/**
 * Copyright (c) 2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.launch;

import org.eclipse.debug.core.ILaunchManager;

import com.piece_framework.makegood.core.MakeGoodCorePlugin;
import com.piece_framework.makegood.core.preference.MakeGoodPreferenceInitializer;

public class RuntimeConfiguration {
    public boolean debugs = false;
    public boolean stopsOnFailure = false;
    public boolean background = false;
    public boolean auto;
    private static RuntimeConfiguration soleInstance;

    public static RuntimeConfiguration getInstance() {
        if (soleInstance == null) {
            soleInstance = new RuntimeConfiguration();
        }

        return soleInstance;
    }

    public String getLaunchMode() {
        return background ? ILaunchManager.RUN_MODE :
                            (debugs ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE);
    }

    private RuntimeConfiguration() {
        auto = MakeGoodCorePlugin.getDefault().getPreferenceStore().getBoolean(
                   MakeGoodPreferenceInitializer.RUN_ALL_TESTS_AUTOMATICALLY
               );
    }
}
