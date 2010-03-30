package com.piece_framework.makegood.launch.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestSuite extends TestResult {
    String file;
    String fullPackage;
    String packageName;
    int testCount;
    int errorCount;
    int failureCount;
    List<TestResult> children;

    TestSuite(Map<String, String> attributes) {
        this.name = attributes.get("name"); //$NON-NLS-1$
        if (attributes.containsKey("file")) { //$NON-NLS-1$
            this.file = attributes.get("file"); //$NON-NLS-1$
        }
        if (attributes.containsKey("fullPackage")) { //$NON-NLS-1$
            this.fullPackage = attributes.get("fullPackage"); //$NON-NLS-1$
        }
        if (attributes.containsKey("package")) { //$NON-NLS-1$
            this.packageName = attributes.get("package"); //$NON-NLS-1$
        }
        if (attributes.containsKey("tests")) { //$NON-NLS-1$
            this.testCount = Integer.parseInt(attributes.get("tests")); //$NON-NLS-1$
        }

        children = new ArrayList<TestResult>();
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

    public int getErrorCount() {
        return errorCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    @Override
    public boolean hasError() {
        for (TestResult result : children) {
            if (result.hasError()) {
                return true;
            }
        }
        return errorCount > 0;
    }

    @Override
    public boolean hasFailure() {
        for (TestResult result : children) {
            if (result.hasFailure()) {
                return true;
            }
        }
        return failureCount > 0;
    }

    @Override
    void addTestResult(TestResult result) {
        if (result == null) {
            return;
        }

        result.parent = this;

        children.add(result);
    }

    @Override
    public List<TestResult> getTestResults() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public TestResult findTestResult(String name) {
        for (TestResult result: children) {
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    public void increaseFailureCount() {
        ++failureCount;
        if (parent != null) {
            ((TestSuite) parent).increaseFailureCount();
        }
    }

    public void increaseErrorCount() {
        ++errorCount;
        if (parent != null) {
            ((TestSuite) parent).increaseErrorCount();
        }
    }

    @Override
    public void setTime(long time) {
        this.time += time;
        if (parent != null) {
            parent.setTime(time);
        }
    }
}
