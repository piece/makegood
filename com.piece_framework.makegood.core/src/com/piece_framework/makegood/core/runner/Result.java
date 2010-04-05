package com.piece_framework.makegood.core.runner;

import java.util.List;

public abstract class Result {
    protected String name;
    protected long time;
    protected String file;
    private Result parent;

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

    public abstract List<Result> getChildren();

    public abstract Result getChild(String name);

    public boolean hasError() {
        return getErrorCount() > 0;
    }

    public boolean hasFailure() {
        return getFailureCount() > 0;
    }

    public abstract void addChild(Result result);

    public Result getParent() {
        return parent;
    }

    public void setParent(Result parent) {
        this.parent = parent;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public abstract int getTestCount();

    public abstract int getErrorCount();

    public abstract int getFailureCount();
}
