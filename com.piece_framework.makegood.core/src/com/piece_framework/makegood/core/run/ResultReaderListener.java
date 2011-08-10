/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core.run;

import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.core.result.TestSuiteResult;

public interface ResultReaderListener {
    public void startTestSuite(TestSuiteResult testSuite);

    public void endTestSuite(TestSuiteResult testSuite);

    public void startTestCase(TestCaseResult testCase);

    public void endTestCase(TestCaseResult testCase);

    public void startFailure(TestCaseResult failure);

    public void endFailure(TestCaseResult failure);

    public void endTest();

    /**
     * @since 1.7.0
     */
    public void startError(TestCaseResult error);

    /**
     * @since 1.7.0
     */
    public void endError(TestCaseResult error);
}
