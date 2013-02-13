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

import com.piece_framework.makegood.ui.views.ResultView;

public class NextFailureHandler extends MoveToFailureHandler {
    /**
     * @since 1.8.0
     */
    @Override
    protected void moveToFailure(ResultView resultView) {
        resultView.moveToNextFailure();
    }
}
