package com.piece_framework.makegood.core.runner;

import java.util.List;

public abstract class TestResult {
    String name;
    long time;
    String file;
    private TestResult parent;

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public String getFile() {
        return file;
    }

    public abstract void setTime(long time);

    public abstract List<TestResult> getTestResults();

    public abstract TestResult findTestResult(String name);

    public abstract boolean hasError();

    public abstract boolean hasFailure();

    abstract void addTestResult(TestResult result);

    public TestResult getParent() {
        return parent;
    }

    public void setParent(TestResult parent) {
        this.parent = parent;
    }
}
