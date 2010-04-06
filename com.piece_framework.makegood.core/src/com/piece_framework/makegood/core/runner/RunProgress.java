package com.piece_framework.makegood.core.runner;

public class RunProgress {
    private boolean isInitialized;
    private long processTime;
    private long startTimeForTestCase;
    private long processTimeForTestCase;
    private TestSuiteResult suite;

    public RunProgress() {
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
        processTimeForTestCase = System.nanoTime() - startTimeForTestCase;
        processTime += processTimeForTestCase;
    }

    public long getProcessTimeForTestCase() {
        return processTimeForTestCase;
    }

    public boolean hasFailures() {
        return suite.hasFailure() || suite.hasError();
    }
}
