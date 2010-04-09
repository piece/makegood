package com.piece_framework.makegood.core.result;

import java.util.List;

public abstract class Result {
    private String name;
    long time;
    private String file;
    Result parent;

    public Result(String name) {
        this.name = name;
    }

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

    public boolean hasErrors() {
        return getErrorCount() > 0;
    }

    public boolean hasFailures() {
        return getFailureCount() > 0;
    }

    void addChild(Result result) {}

    void setFile(String file) {
        this.file = file;
    }

    public abstract int getTestCount();

    public abstract int getErrorCount();

    public abstract int getFailureCount();
}
