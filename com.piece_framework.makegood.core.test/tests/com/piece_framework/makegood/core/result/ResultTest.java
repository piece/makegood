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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ResultTest {
    @Test
    public void providesTheNumberOfElementsOfATestCase() {
        assertEquals(1, new TestCaseResult("foo").getSize());
    }

    @Test
    public void providesTheNumberOfElementsOfATestSuite() {
        assertEquals(1, new TestSuiteResult("foo").getSize());
    }

    @Test
    public void providesTheNumberOfElementsOfATestSuiteWithATestCase() {
        Result result = new TestSuiteResult("foo");
        result.addChild(new TestCaseResult("bar"));
        assertEquals(2, result.getSize());
    }

    @Test
    public void providesTheNumberOfElementsOfATestSuiteWithATestSuite() {
        Result result = new TestSuiteResult("foo");
        result.addChild(new TestSuiteResult("bar"));
        assertEquals(2, result.getSize());
    }
}
