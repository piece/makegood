package com.piece_framework.makegood.core.runner;

import java.util.List;

public abstract class TestResult {
    protected String name;
    protected long time;
    protected String file;
    private TestResult parent;

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public String getFile() {
        return file;
    }

    public abstract void setTime(long time);

    public abstract List<TestResult> getChildren();

    public abstract TestResult getChild(String name);

    public abstract boolean hasError();

    public abstract boolean hasFailure();

    public abstract void addChild(TestResult result);

    public TestResult getParent() {
        return parent;
    }

    public void setParent(TestResult parent) {
        this.parent = parent;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
