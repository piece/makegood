package com.piece_framework.makegood.core.runner;

public class RunProgress {
    private boolean initialized;
    private long totalTime;
    private long startTimeForTestCase;
    private long testCaseTime;
    private TestSuiteResult suite;

    public void initialize(TestSuiteResult suite) {
        this.suite = suite;
        startTimeForTestCase = System.nanoTime();
        totalTime = 0;
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void finalize() {}

    public int getAllTestCount() {
        return suite.getAllTestCount();
    }

    public int getEndTestCount() {
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

    public long getTotalTime() {
        return totalTime;
    }

    public int getRate() {
        if (suite.getAllTestCount() == 0) {
            return 0;
        }

        int rate = (int) (((double) suite.getTestCount() / (double) suite.getAllTestCount()) * 100d);
        return rate <= 100 ? rate : 100;
    }

    public long getAverage() {
        if (suite.getTestCount() == 0) {
            return 0;
        }

        return getTotalTime() / suite.getTestCount();
    }

    public void startTestCase() {
        startTimeForTestCase = System.nanoTime();
    }

    public void endTestCase() {
        testCaseTime = System.nanoTime() - startTimeForTestCase;
        totalTime += testCaseTime;
    }

    public long getTestCaseTime() {
        return testCaseTime;
    }
}
