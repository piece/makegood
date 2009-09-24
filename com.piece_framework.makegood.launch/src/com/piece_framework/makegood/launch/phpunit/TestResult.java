package com.piece_framework.makegood.launch.phpunit;

import java.util.ArrayList;
import java.util.List;

public abstract class TestResult {
    String name;
    List<TestResult> results;

    public String getName() {
        return name;
    }

    public TestResult findResult(String name) {
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

    void addResult(TestResult result) {
        if (results == null) {
            results = new ArrayList<TestResult>();
        }
        results.add(result);
    }
}
