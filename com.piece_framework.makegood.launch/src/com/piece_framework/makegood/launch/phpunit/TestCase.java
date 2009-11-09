package com.piece_framework.makegood.launch.phpunit;

import java.util.Map;

public class TestCase extends TestResult {
    String file;
    String targetClass;
    int assertionCount;
    int line;
    Problem problem;

    TestCase(Map<String, String> attributes) {
        this.name = attributes.get("name");
        if (attributes.containsKey("class")) {
            this.targetClass = attributes.get("class");
        }
        if (attributes.containsKey("file")) {
            this.file = attributes.get("file");
        }
        if (attributes.containsKey("line")) {
            this.line = Integer.parseInt(attributes.get("line"));
        }
        if (attributes.containsKey("assertions")) {
            this.assertionCount = Integer.parseInt(attributes.get("assertions"));
        }
        if (attributes.containsKey("time")) {
            this.time = Double.parseDouble(attributes.get("time"));
        }
        this.problem = new Problem(ProblemType.Pass);
    }

    public String getFile() {
        return file;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public int getAssertionCount() {
        return assertionCount;
    }

    public int getLine() {
        return line;
    }

    public double getTime() {
        return time;
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
}
