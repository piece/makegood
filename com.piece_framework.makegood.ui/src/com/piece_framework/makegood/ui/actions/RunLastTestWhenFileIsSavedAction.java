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

package com.piece_framework.makegood.ui.actions;

import com.piece_framework.makegood.core.AutotestScope;

public class RunLastTestWhenFileIsSavedAction extends ToggleAutotestAction {
    public static final String ACTION_ID = "com.piece_framework.makegood.ui.viewActions.runLastTestWhenFileIsSavedAction"; //$NON-NLS-1$

    /**
     * @since 2.1.0
     */
    @Override
    protected AutotestScope getAutotestScope() {
        return AutotestScope.LAST_TEST;
    }
}
