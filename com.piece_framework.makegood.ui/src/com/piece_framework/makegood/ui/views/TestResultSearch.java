package com.piece_framework.makegood.ui.views;

import com.piece_framework.makegood.launch.phpunit.TestCase;
import com.piece_framework.makegood.launch.phpunit.TestResult;
import com.piece_framework.makegood.launch.phpunit.TestSuite;

public class TestResultSearch {
    public TestCase getNextFailure(java.util.List<TestResult> results,
                                   TestResult selected,
                                   boolean findSelected
                                   ) {
        boolean find = findSelected;
        for (TestResult result: results) {
            if (!find) {
                if (result == selected) {
                    System.out.println(result.getName());
                    find = true;
                }
            }

            if (result instanceof TestSuite) {
                getNextFailure(result.getTestResults(), selected, find);
            }
        }
        return null;
    }
}
