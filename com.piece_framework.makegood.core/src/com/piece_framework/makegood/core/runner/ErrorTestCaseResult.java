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

package com.piece_framework.makegood.core.runner;

public class ErrorTestCaseResult extends TestCaseResult {
    public ErrorTestCaseResult(String name) {
        super(name);
    }

    public ErrorTestCaseResult(TestCaseResult currentTestCase) {
        super(currentTestCase.name);
        setFile(currentTestCase.getFile());
        setClassName(currentTestCase.getClassName());
        setLine(currentTestCase.getLine());
    }

    @Override
    public boolean hasError() {
        return true;
    }

    @Override
    public int getErrorCount() {
        return 1;
    }
}
