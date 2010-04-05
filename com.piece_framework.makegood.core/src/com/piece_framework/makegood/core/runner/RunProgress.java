package com.piece_framework.makegood.core.runner;

public class RunProgress {
    private boolean isInitialized;
    private long processTime;
    private long startTimeForTestCase;
    private long testCaseTime;
    private TestSuiteResult suite;

    public void initialize(TestSuiteResult suite) {
        this.suite = suite;
        startTimeForTestCase = System.nanoTime();
        processTime = 0;
        isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
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

    public long getProcessTime() {
        return processTime;
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

        return getProcessTime() / suite.getTestCount();
    }

    public void startTestCase() {
        startTimeForTestCase = System.nanoTime();
    }

    public void endTestCase() {
        testCaseTime = System.nanoTime() - startTimeForTestCase;
        processTime += testCaseTime;
    }

    public long getTestCaseTime() {
        return testCaseTime;
    }
}
