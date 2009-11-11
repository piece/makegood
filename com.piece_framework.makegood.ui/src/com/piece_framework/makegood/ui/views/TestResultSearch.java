package com.piece_framework.makegood.ui.views;

import java.util.List;

import com.piece_framework.makegood.launch.phpunit.TestCase;
import com.piece_framework.makegood.launch.phpunit.TestResult;
import com.piece_framework.makegood.launch.phpunit.TestSuite;

public class TestResultSearch {
    private List<TestResult> results;
    private TestResult selected;
    private TestResult findSelected;

    public TestResultSearch(List<TestResult> results,
                            TestResult selected
                            ) {
        this.results = results;
        this.selected = selected;
    }

    public TestCase getNextFailure(boolean findSelected) {
        this.findSelected = null;
        return getNextFailure(results);
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
}
