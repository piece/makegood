package com.piece_framework.makegood.ui.views;

import com.piece_framework.makegood.launch.elements.ProblemType;

public class TestProgress {
    private int allTestCount;
    private int endTestCount;
    private boolean initialized;
    private int passCount;
    private int failureCount;
    private int errorCount;
    private long totalTime;
    private long startTimeForTestCase;
    private long testCaseTime;

    public void initialize(int allTestCount) {
        this.allTestCount = allTestCount;
        endTestCount = 0;
        passCount = 0;
        failureCount = 0;
        errorCount = 0;

        startTimeForTestCase = System.currentTimeMillis();
        totalTime = 0;

        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void finalize() {
    }

    public int getAllTestCount() {
        return allTestCount;
    }

    public int getEndTestCount() {
        return endTestCount;
    }

    public int getPassCount() {
        return passCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public double getTotalTime() {
        return totalTime / 1000d;
    }

    public int getRate() {
        int rate = (int) (((double) endTestCount / (double) allTestCount) * 100d);
        return rate <= 100 ? rate : 100;
    }

    public double getAverage() {
        return getTotalTime() / (double) endTestCount;
    }

    public void incrementEndTestCount() {
        ++endTestCount;
    }

    public void incrementResultCount(ProblemType problemType) {
        if (problemType == ProblemType.Pass) {
            ++passCount;
        } else if (problemType == ProblemType.Failure) {
            ++failureCount;
        } else if (problemType == ProblemType.Error) {
            ++errorCount;
        }
    }

    public void startTestCase() {
        startTimeForTestCase = System.currentTimeMillis();
    }

    public void endTestCase() {
        testCaseTime = System.currentTimeMillis() - startTimeForTestCase;
        totalTime += testCaseTime;
    }

    public double getTestCaseTime() {
        return testCaseTime / 1000d;
    }
}
