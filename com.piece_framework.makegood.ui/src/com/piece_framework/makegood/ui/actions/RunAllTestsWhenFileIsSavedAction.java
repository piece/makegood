/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2011-2012 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.actions;

import com.piece_framework.makegood.core.AutotestScope;

public class RunAllTestsWhenFileIsSavedAction extends ToggleAutotestAction {
    public static final String ACTION_ID = "com.piece_framework.makegood.ui.viewActions.runAllTestsWhenFileIsSavedAction"; //$NON-NLS-1$

    /**
     * @since 2.1.0
     */
    @Override
    protected AutotestScope getAutotestScope() {
        return AutotestScope.ALL_TESTS;
    }
}
