package com.piece_framework.makegood.ui.views;

import java.util.List;

import com.piece_framework.makegood.launch.phpunit.TestCase;
import com.piece_framework.makegood.launch.phpunit.TestResult;
import com.piece_framework.makegood.launch.phpunit.TestSuite;

public class TestResultSearch {
    private List<TestResult> results;
    private TestResult selected;
    private TestResult findSelected;
    private TestCase lastFailure;

    public TestResultSearch(List<TestResult> results,
                            TestResult selected
                            ) {
        this.results = results;
        this.selected = selected;
    }

    public TestCase getNextFailure() {
        findSelected = null;
        return getNextFailure(results);
    }

    public TestCase getPreviousFailure() {
        findSelected = null;
        lastFailure = null;
        return getPreviousFailure(results);
    }

    private TestCase getNextFailure(List<TestResult> targets) {
        for (TestResult result: targets) {
            if (findSelected == null) {
                if (result.getName().equals(selected.getName())) {
                    findSelected = result;
                }
            } else {
                if (result instanceof TestCase
                    && (result.hasError() || result.hasFailure())
                    ) {
                    return (TestCase) result;
                }
            }

            if (result instanceof TestSuite) {
                TestCase testCase = getNextFailure(result.getTestResults());
                if (testCase != null) {
                    return testCase;
                }
            }
        }
        return null;
    }

    private TestCase getPreviousFailure(List<TestResult> targets) {
        for (TestResult result: targets) {
            if (findSelected == null) {
                if (result.getName().equals(selected.getName())) {
                    return lastFailure;
                } else {
                    if (result instanceof TestCase
                        && (result.hasError() || result.hasFailure())
                        ) {
                        lastFailure = (TestCase) result;
                    }
                }
            }

            if (result instanceof TestSuite) {
                TestCase testCase = getPreviousFailure(result.getTestResults());
                if (testCase != null) {
                    return testCase;
                }
            }
        }
        return null;
    }
}
