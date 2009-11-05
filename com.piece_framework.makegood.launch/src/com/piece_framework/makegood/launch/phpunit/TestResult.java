package com.piece_framework.makegood.launch.phpunit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TestResult {
    String name;
    List<TestResult> results;

    public String getName() {
        return name;
    }

    public List<TestResult> getTestResults() {
        if (results == null) {
            results = new ArrayList<TestResult>();
        }
        return Collections.unmodifiableList(results);
    }

    public TestResult findTestResult(String name) {
        if (results == null) {
            return null;
        }

        for (TestResult result: results) {
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    public boolean hasErrorChild() {
        if (results == null) {
            return false;
        }

        for (TestResult result: results) {
            if (result.hasErrorChild()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasError() {
        if (results == null) {
            return false;
        }

        for (TestResult result: results) {
            if (result.hasError()) {
                return true;
            }
        }
        return false;
    }

    void addTestResult(TestResult result) {
        if (result == null) {
            return;
        }

        if (results == null) {
            results = new ArrayList<TestResult>();
        }
        results.add(result);
    }
}
