package com.piece_framework.makegood.launch.elements;

import java.util.List;

public abstract class TestResult {
    String name;
    double time;
    TestResult parent;

    public String getName() {
        return name;
    }

    public double getTime() {
        return time;
    }

    public abstract void setTime(double time);

    public abstract List<TestResult> getTestResults();

    public abstract TestResult findTestResult(String name);

    public abstract boolean hasError();

    public abstract boolean hasFailure();

    abstract void addTestResult(TestResult result);
}
