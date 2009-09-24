package com.piece_framework.makegood.launch.phpunit;

public class TestCase extends TestResult {
    String file;
    String className;
    int assertionCount;
    int line;
    double time;
    Failure failure;

    public String getFile() {
        return file;
    }

    public String getClassName() {
        return className;
    }

    public int getAssertionCount() {
        return assertionCount;
    }

    public int getLine() {
        return line;
    }

    public double getTime() {
        return time;
    }

    public Failure getFailure() {
        return failure;
    }
}
