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

package com.piece_framework.makegood.core.run;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.piece_framework.makegood.core.result.TestSuiteResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ProgressRateTest {
    private int testCount;
    private int allTestCount;
    private int rate;

    public ProgressRateTest(int testCount, int allTestCount, int rate) {
        this.testCount = testCount;
        this.allTestCount = allTestCount;
        this.rate = rate;
    }

    @Parameters
    public static List<Object[]> data() {
        Object[][] data = new Object[][] {
            { 9, 8, 100 },
            { 8, 8, 100 },
            { 4, 8, 50 },
            { 2, 8, 25 },
            { 1, 8, 12 },
            { 0, 8, 0 },
            { 0, 0, 0 },
        };
        return Arrays.asList(data);
    }

    @Test
    public void calculatesTheProgressRate() {
        TestSuiteResult testSuite = mock(TestSuiteResult.class);
        when(testSuite.getTestCount()).thenReturn(testCount);
        when(testSuite.getAllTestCount()).thenReturn(allTestCount);
        Progress progress = new Progress();
        progress.onFirstTestSuite(testSuite);
        assertEquals(rate, progress.calculateRate());
    }
}
