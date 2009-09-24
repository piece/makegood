package com.piece_framework.makegood.launch.phpunit;

public class TestCase extends TestResult {
    private String file;
    private String className;
    private int assertionCount;
    private int line;
    private double time;
    private Failure failure;

    public String getFile() {
        return file;
    }

    void setFile(String file) {
        this.file = file;
    }

    public String getClassName() {
        return className;
    }

    void setClassName(String className) {
        this.className = className;
    }

    public int getAssertionCount() {
        return assertionCount;
    }

    void setAssertionCount(int assertionCount) {
        this.assertionCount = assertionCount;
    }

    public int getLine() {
        return line;
    }

    void setLine(int line) {
        this.line = line;
    }

    public double getTime() {
        return time;
    }

    void setTime(double time) {
        this.time = time;
    }

    public Failure getFailure() {
        return failure;
    }

    void setFailure(Failure failure) {
        this.failure = failure;
    }
}
