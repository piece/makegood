/**
 * Copyright (c) 2010-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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

import com.piece_framework.makegood.core.continuoustesting.ContinuousTesting;
import com.piece_framework.makegood.core.preference.MakeGoodPreference;

public class RuntimeConfiguration {
    public boolean debugsTest = false;
    public boolean stopsOnFailure = false;
    public boolean showsOnlyFailures = false;

    /**
     * @since 2.3.0
     */
    private ContinuousTesting continuousTesting;

    private static RuntimeConfiguration soleInstance;

    public static RuntimeConfiguration getInstance() {
        if (soleInstance == null) {
            soleInstance = new RuntimeConfiguration();
        }

        return soleInstance;
    }

    public String getLaunchMode() {
        return debugsTest ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE;
    }

    private RuntimeConfiguration() {
        MakeGoodPreference preference = new MakeGoodPreference();
        continuousTesting = new ContinuousTesting(preference.getContinuousTestingEnabled(), preference.getContinuousTestingScope());
    }

    /**
     * @since 2.3.0
     */
    public void setContinuousTesting(ContinuousTesting continuousTesting) {
        this.continuousTesting = continuousTesting;
    }

    /**
     * @since 2.3.0
     */
    public ContinuousTesting getContinuousTesting() {
        return continuousTesting;
    }
}
