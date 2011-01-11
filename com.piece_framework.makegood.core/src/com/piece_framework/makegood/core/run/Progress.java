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
    private TestSuiteResult suite;
    private long startTime;
    private long endTime;
    private boolean isRunning = false;
    private boolean isCompleted = false;

    public Progress() {
        suite = new TestSuiteResult(null);
    }

    public void initialize(TestSuiteResult suite) {
        this.suite = suite;
        startTimeForTestCase = System.nanoTime();
        processTime = 0;
        isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public int getAllTestCount() {
        return suite.getAllTestCount();
    }

    public int getTestCount() {
        return suite.getTestCount();
    }

    public int getPassCount() {
        return suite.getPassCount();
    }

    public int getFailureCount() {
        return suite.getFailureCount();
    }

    public int getErrorCount() {
        return suite.getErrorCount();
    }

    public long getProcessTime() {
        return processTime;
    }

    public int calculateRate() {
        if (suite.getAllTestCount() == 0) {
            return 0;
        }

        int rate = (int) (((double) suite.getTestCount() / (double) suite.getAllTestCount()) * 100d);
        return rate <= 100 ? rate : 100;
    }

    public long calculateProcessTimeAverage() {
        if (suite.getTestCount() == 0) {
            return 0;
        }

        return getProcessTime() / suite.getTestCount();
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
        return suite.hasFailures() || suite.hasErrors();
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

    public boolean isCompleted() {
        return isCompleted;
    }
}
