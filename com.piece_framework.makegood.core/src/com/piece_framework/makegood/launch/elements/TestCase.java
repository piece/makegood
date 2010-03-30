package com.piece_framework.makegood.launch.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestCase extends TestResult {
    String file;
    String targetClass;
    int line;
    Problem problem;

    TestCase(Map<String, String> attributes) {
        this.name = attributes.get("name"); //$NON-NLS-1$
        if (attributes.containsKey("class")) { //$NON-NLS-1$
            this.targetClass = attributes.get("class"); //$NON-NLS-1$
        }
        if (attributes.containsKey("file")) { //$NON-NLS-1$
            this.file = attributes.get("file"); //$NON-NLS-1$
        }
        if (attributes.containsKey("line")) { //$NON-NLS-1$
            this.line = Integer.parseInt(attributes.get("line")); //$NON-NLS-1$
        }
        this.problem = new Problem(ProblemType.Pass);
    }

    public String getFile() {
        return file;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public int getLine() {
        return line;
    }

    public Problem getProblem() {
        return problem;
    }

    @Override
    public boolean hasError() {
        return problem.getType() == ProblemType.Error;
    }

    @Override
    public boolean hasFailure() {
        return problem.getType() == ProblemType.Failure;
    }

    @Override
    void addTestResult(TestResult result) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TestResult> getTestResults() {
        return Collections.unmodifiableList(new ArrayList<TestResult>());
    }

    @Override
    public TestResult findTestResult(String name) {
        return null;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
        parent.setTime(time);
    }
}
