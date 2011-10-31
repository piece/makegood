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

public class Progress implements ResultReaderListener {
    private long processTime;
    private long startTimeForTestCase;
    private TestSuiteResult testSuite = new TestSuiteResult(null);
    private long startTime;
    private long endTime;
    private boolean isRunning = false;
    private boolean isCompleted = false;

    /**
     * @since 1.8.0
     */
    private boolean isStopped = false;

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
        if (getAllTestCount() == 0) {
            return 0;
        }

        int rate = (int) (((double) getTestCount() / (double) getAllTestCount()) * 100d);
        return rate <= 100 ? rate : 100;
    }

    public long calculateProcessTimeAverage() {
        if (getTestCount() == 0) {
            return 0;
        }

        return getProcessTime() / getTestCount();
    }

    @Override
    public void startTestCase(TestCaseResult testCase) {
        startTimeForTestCase = System.nanoTime();
    }

    @Override
    public void endTestCase(TestCaseResult testCase) {
        long processTimeForTestCase = System.nanoTime() - startTimeForTestCase;
        processTime += processTimeForTestCase;
        testCase.setTime(processTimeForTestCase);
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

    private void markAsCompleted() {
        isCompleted = true;
    }

    /**
     * @since 1.8.0
     */
    public void markAsStopped() {
        isStopped  = true;
    }

    /**
     * @since 1.8.0
     */
    public boolean isStopped() {
        return isStopped;
    }

    /**
     * @since 1.3.0
     */
    public boolean noTestsFound() {
        return isCompleted && getAllTestCount() == 0;
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void onFirstTestSuite(TestSuiteResult testSuite) {
        this.testSuite = testSuite;
        processTime = 0;
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void startTestSuite(TestSuiteResult testSuite) {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void endTestSuite(TestSuiteResult testSuite) {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void startFailure(TestCaseResult failure) {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void endFailure(TestCaseResult failure) {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void startTest() {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void endTest() {
        markAsCompleted();
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void startError(TestCaseResult error) {
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void endError(TestCaseResult error) {
    }

    /**
     * @since 1.8.0
     */
    public TestSuiteResult getResult() {
        return testSuite;
    }
}
