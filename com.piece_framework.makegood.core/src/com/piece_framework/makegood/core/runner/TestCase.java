package com.piece_framework.makegood.core.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestCase extends TestResult {
    private String className;
    private int line;
    private Problem problem;
    private boolean isArtificial = false;

    public TestCase(String name) {
        this.name = name;
        setProblem(new Problem(ProblemType.Pass));
    }

    public String getClassName() {
        return className;
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
    public List<TestResult> getChildren() {
        return Collections.unmodifiableList(new ArrayList<TestResult>());
    }

    @Override
    public TestResult getChild(String name) {
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

    public void setClassName(String cassName) {
        this.className = cassName;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public boolean isArtificial() {
        return isArtificial;
    }

    public void setIsArtificial(boolean isArtificial) {
        this.isArtificial = isArtificial;
    }
}
