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

package com.piece_framework.makegood.core.result;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestCaseResultTest {
    @Test
    public void providesTheFileOfTheParentTestSuiteIfTheFileIsNotGiven() {
        String expectedFile = "/path/to/file";
        TestSuiteResult testSuite = new TestSuiteResult("foo");
        testSuite.setFile(expectedFile);
        TestCaseResult testCase = new TestCaseResult("bar");
        testSuite.addChild(testCase);
        assertEquals(expectedFile, testCase.getFile());
    }
}
