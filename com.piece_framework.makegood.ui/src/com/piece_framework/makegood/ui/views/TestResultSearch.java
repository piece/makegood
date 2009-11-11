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
        boolean find = findSelected;
        for (TestResult result: results) {
            if (!find) {
                if (result == selected) {
                    System.out.println(result.getName());
                    find = true;
                }
            }

            if (result instanceof TestSuite) {
                getNextFailure(find);
            }
        }
        return null;
    }
}
