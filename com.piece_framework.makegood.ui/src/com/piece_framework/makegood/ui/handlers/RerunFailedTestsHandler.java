/**
 * Copyright (c) 2012 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * @since 2.1.0
 */
public class RerunFailedTestsHandler extends RunHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        getTestRunner().rerunFailedTests();
        return null;
    }

    @Override
    protected boolean doIsEnabled() {
        return getTestRunner().hasLastTest();
    }
}
