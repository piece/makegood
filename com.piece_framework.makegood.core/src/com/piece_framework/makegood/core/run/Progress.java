/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core.run;

import com.piece_framework.makegood.core.result.TestSuiteResult;

public class Progress {
    private boolean isInitialized;
    private long processTime;
    private long startTimeForTestCase;
    private long processTimeForTestCase;
    private TestSuiteResult testSuite;
    private long startTime;
    private long endTime;
    private boolean isRunning = false;
    private boolean isCompleted = false;

    public Progress() {
        testSuite = new TestSuiteResult(null);
    }

    public void initialize(TestSuiteResult testSuite) {
        this.testSuite = testSuite;
        startTimeForTestCase = System.nanoTime();
        processTime = 0;
        isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public int getAllTestCount() {
        return testSuite.getAllTestCount();
    }

    public int getTestCount() {
        return testSuite.getTestCount();
    }

    public int getPassCount() {
        return testSuite.getPassCount();
    }

    public int getFailureCount() {
        return testSuite.getFailureCount();
    }

    public int getErrorCount() {
        return testSuite.getErrorCount();
    }

    public long getProcessTime() {
        return processTime;
    }

    public int calculateRate() {
        if (testSuite.getAllTestCount() == 0) {
            return 0;
        }

        int rate = (int) (((double) testSuite.getTestCount() / (double) testSuite.getAllTestCount()) * 100d);
        return rate <= 100 ? rate : 100;
    }

    public long calculateProcessTimeAverage() {
        if (testSuite.getTestCount() == 0) {
            return 0;
        }

        return getProcessTime() / testSuite.getTestCount();
    }

    public void startTestCase() {
        startTimeForTestCase = System.nanoTime();
    }

    public void endTestCase() {
        processTimeForTestCase = System.nanoTime() - startTimeForTestCase;
        processTime += processTimeForTestCase;
    }

    public long getProcessTimeForTestCase() {
        return processTimeForTestCase;
    }

    public boolean hasFailures() {
        return testSuite.hasFailures() || testSuite.hasErrors();
    }

    public void start() {
        isRunning = true;
        startTime = System.nanoTime();
    }

    public void end() {
        endTime = System.nanoTime();
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public long getElapsedTime() {
        if (isRunning) {
            return System.nanoTime() - startTime;
        } else {
            return endTime - startTime;
        }
    }

    public void markAsCompleted() {
        isCompleted = true;
    }

    /**
     * @since 1.3.0
     */
    public boolean noTestsFound() {
        return isCompleted && getAllTestCount() == 0;
    }
}
