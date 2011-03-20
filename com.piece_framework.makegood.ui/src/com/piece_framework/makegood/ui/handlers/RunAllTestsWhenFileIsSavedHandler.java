/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.handlers;

import com.piece_framework.makegood.ui.actions.RunAllTestsWhenFileIsSavedAction;

public class RunAllTestsWhenFileIsSavedHandler extends ToggleHandler {
    @Override
    protected String getActionId() {
        return RunAllTestsWhenFileIsSavedAction.ACTION_ID;
    }
}
