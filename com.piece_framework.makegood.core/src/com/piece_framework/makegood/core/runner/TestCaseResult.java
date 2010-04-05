package com.piece_framework.makegood.core.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestCaseResult extends Result {
    private String className;
    private int line;
    private boolean isArtificial = false;
    private String failureType;
    private String failureTrace;

    public TestCaseResult(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public int getLine() {
        return line;
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public boolean hasFailure() {
        return false;
    }

    @Override
    public void addChild(Result result) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Result> getChildren() {
        return Collections.unmodifiableList(new ArrayList<Result>());
    }

    @Override
    public Result getChild(String name) {
        return null;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
        getParent().setTime(time);
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

    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    public String getFailureType() {
        return failureType;
    }

    public void setFailureTrace(String failureTrace) {
        this.failureTrace = failureTrace;
    }

    public String getFailureTrace() {
        return failureTrace;
    }
}
