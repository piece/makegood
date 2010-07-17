/**
 * Copyright (c) 2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core.result;

public class ErrorTestCaseResult extends TestCaseResult {
    public ErrorTestCaseResult(String name) {
        super(name);
    }

    public ErrorTestCaseResult(TestCaseResult currentTestCase) {
        super(currentTestCase.getName());
        setFile(currentTestCase.getFile());
        setClassName(currentTestCase.getClassName());
        setLine(currentTestCase.getLine());
    }

    @Override
    public boolean hasErrors() {
        return true;
    }

    @Override
    public int getErrorCount() {
        if (!fixed) return 0;
        return 1;
    }
}
