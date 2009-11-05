package com.piece_framework.makegood.launch.phpunit;

import java.util.Map;

public class TestSuite extends TestResult {
    String file;
    String fullPackage;
    String packageName;
    int testCount;
    int assertionCount;
    int errorCount;
    int failureCount;
    double time;

    TestSuite(Map<String, String> attributes) {
        this.name = attributes.get("name");
        if (attributes.containsKey("file")) {
            this.file = attributes.get("file");
        }
        if (attributes.containsKey("fullPackage")) {
            this.fullPackage = attributes.get("fullPackage");
        }
        if (attributes.containsKey("package")) {
            this.packageName = attributes.get("package");
        }
        if (attributes.containsKey("tests")) {
            this.testCount = Integer.parseInt(attributes.get("tests"));
        }
        if (attributes.containsKey("assertions")) {
            this.assertionCount = Integer.parseInt(attributes.get("assertions"));
        }
        if (attributes.containsKey("errors")) {
            this.errorCount = Integer.parseInt(attributes.get("errors"));
        }
        if (attributes.containsKey("failures")) {
            this.failureCount = Integer.parseInt(attributes.get("failures"));
        }
        if (attributes.containsKey("time")) {
            this.time = Double.parseDouble(attributes.get("time"));
        }
    }

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

    @Override
    public boolean hasError() {
        boolean result = super.hasError();
        if (!result) {
            result = errorCount > 0;
        }
        return result;
    }

    @Override
    public boolean hasFailure() {
        boolean result = super.hasFailure();
        if (!result) {
            result = failureCount > 0;
        }
        return result;
    }
}
