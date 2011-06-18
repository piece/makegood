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

import com.piece_framework.makegood.ui.views.ResultView;
import com.piece_framework.makegood.ui.views.ViewOpener;

public class NextFailureHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ResultView view = (ResultView) ViewOpener.find(ResultView.VIEW_ID);
        if (view != null) {
            view.moveToNextFailure();
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        if (!super.isEnabled()) return false;
        ResultView view = (ResultView) ViewOpener.find(ResultView.VIEW_ID);
        if (view == null) return false;
        return view.hasFailures();
    }
}
