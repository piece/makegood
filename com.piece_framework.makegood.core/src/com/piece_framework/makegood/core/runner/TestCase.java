package com.piece_framework.makegood.core.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestCase extends TestResult {
    private String targetClass;
    private int line;
    private Problem problem;

    public TestCase(Map<String, String> attributes) {
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
        setProblem(new Problem(ProblemType.Pass));
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
        return getProblem().getType() == ProblemType.Error;
    }

    @Override
    public boolean hasFailure() {
        return getProblem().getType() == ProblemType.Failure;
    }

    @Override
    public void addChild(TestResult result) throws UnsupportedOperationException {
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
        getParent().setTime(time);
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}
