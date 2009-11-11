package com.piece_framework.makegood.ui.views;

import java.util.List;

import com.piece_framework.makegood.launch.phpunit.TestCase;
import com.piece_framework.makegood.launch.phpunit.TestResult;
import com.piece_framework.makegood.launch.phpunit.TestSuite;

public class TestResultSearch {
    private List<TestResult> results;
    private TestResult selected;

    public TestResultSearch(List<TestResult> results,
                            TestResult selected
                            ) {
        this.results = results;
        this.selected = selected;
    }

    public TestCase getNextFailure(boolean findSelected) {
        return getNextFailure(results, null);
    }

    private TestCase getNextFailure(List<TestResult> targets,
                                    TestResult findSelected
                                    ) {
        for (TestResult result: targets) {
            if (findSelected == null) {
                if (result == selected) {
                    findSelected = result;
                    System.out.println(result.getName());
                }
            }

            if (result instanceof TestSuite) {
                getNextFailure(result.getTestResults(), findSelected);
            }
        }
        return null;
    }
}
