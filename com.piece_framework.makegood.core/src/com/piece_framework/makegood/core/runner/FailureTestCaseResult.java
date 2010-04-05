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

public class FailureTestCaseResult extends TestCaseResult {
    public FailureTestCaseResult(String name) {
        super(name);
    }

    public FailureTestCaseResult(TestCaseResult currentTestCase) {
        super(currentTestCase.name);
        setFile(currentTestCase.getFile());
        setClassName(currentTestCase.getClassName());
        setLine(currentTestCase.getLine());
    }

    @Override
    public boolean hasFailure() {
        return true;
    }

    @Override
    public int getFailureCount() {
        return 1;
    }
}
