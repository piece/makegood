/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.piece_framework.makegood.aspect.AspectWeaver;
import com.piece_framework.makegood.ui.launch.TestRunner;
import com.piece_framework.makegood.ui.views.ActivePart;

public class RunAllTestsHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        TestRunner.getInstance().runAllTests();
        return null;
    }

    @Override
    public boolean isEnabled() {
        if (!AspectWeaver.isFinished()) return false;
        return ActivePart.getInstance().isAllTestsRunnable();
    }
}
