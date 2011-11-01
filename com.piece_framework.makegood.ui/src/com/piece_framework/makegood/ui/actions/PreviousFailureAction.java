/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.actions;

import com.piece_framework.makegood.ui.views.ResultView;

public class PreviousFailureAction extends FailureAction {
    public static final String ACTION_ID = "com.piece_framework.makegood.ui.viewActions.previousFailureAction"; //$NON-NLS-1$

    /**
     * @since 1.8.0
     */
    @Override
    protected void moveToFailure(ResultView resultView) {
        resultView.moveToPreviousFailure();
    }
}
