package com.piece_framework.makegood.core.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestCaseResult extends Result {
    private String className;
    private int line;
    private boolean isArtificial = false;
    String failureType;
    private String failureTrace;

    public TestCaseResult(String name) {
        super(name);
    }

    String getClassName() {
        return className;
    }

    public int getLine() {
        return line;
    }

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public boolean hasFailures() {
        return false;
    }

    @Override
    public List<Result> getChildren() {
        return Collections.unmodifiableList(new ArrayList<Result>());
    }

    @Override
    public void setTime(long time) {
        this.time = time;
        parent.setTime(time);
    }

    void setClassName(String cassName) {
        this.className = cassName;
    }

    void setLine(int line) {
        this.line = line;
    }

    boolean isArtificial() {
        return isArtificial;
    }

    void setIsArtificial(boolean isArtificial) {
        this.isArtificial = isArtificial;
    }

    void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    void setFailureTrace(String failureTrace) {
        this.failureTrace = failureTrace;
    }

    public String getFailureTrace() {
        return failureTrace;
    }

    @Override
    public int getTestCount() {
        return 1;
    }

    @Override
    public int getErrorCount() {
        return 0;
    }

    @Override
    public int getFailureCount() {
        return 0;
    }
}
