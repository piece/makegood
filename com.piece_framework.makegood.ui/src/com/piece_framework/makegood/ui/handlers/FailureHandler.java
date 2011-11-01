/**
 * Copyright (c) 2011 KUBO Atsuhiro <kubo@iteman.jp>,
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

import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.ui.views.ResultView;
import com.piece_framework.makegood.ui.views.ViewOpener;

/**
 * @since 1.8.0
 */
public abstract class FailureHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        TestLifecycle testLifecycle = TestLifecycle.getInstance();
        if (testLifecycle != null && testLifecycle.getProgress().hasFailures()) {
            ResultView resultView = (ResultView) ViewOpener.open(ResultView.VIEW_ID);
            if (resultView != null) {
                moveToFailure(resultView);
            }
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        if (!super.isEnabled()) return false;
        TestLifecycle testLifecycle = TestLifecycle.getInstance();
        return testLifecycle != null && testLifecycle.getProgress().hasFailures();
    }

    protected abstract void moveToFailure(ResultView resultView);
}
