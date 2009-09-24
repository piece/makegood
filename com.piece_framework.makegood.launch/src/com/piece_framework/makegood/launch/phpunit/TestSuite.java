package com.piece_framework.makegood.launch.phpunit;

public class TestSuite extends TestResult {
    String file;
    String fullPackage;
    String packageName;
    int testCount;
    int assertionCount;
    int errorCount;
    int failureCount;
    double time;

    public String getFile() {
        return file;
    }

    public String getFullPackage() {
        return fullPackage;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getTestCount() {
        return testCount;
    }

    public int getAssertionCount() {
        return assertionCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public double getTime() {
        return time;
    }
}
