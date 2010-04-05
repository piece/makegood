package com.piece_framework.makegood.core.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestSuiteResult extends Result {
    private String fullPackageName;
    private String packageName;
    private List<Result> children;
    private int allTestCount;

    public TestSuiteResult(String name) {
        this.name = name;
        children = new ArrayList<Result>();
    }

    public String getFullPackageName() {
        return fullPackageName;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public int getTestCount() {
        int count = 0;
        for (Result result : children) {
            count += result.getTestCount();
        }

        return count;
    }

    @Override
    public int getErrorCount() {
        int count = 0;
        for (Result result : children) {
            count += result.getErrorCount();
        }

        return count;
    }

    @Override
    public int getFailureCount() {
        int count = 0;
        for (Result result : children) {
            count += result.getFailureCount();
        }

        return count;
    }

    @Override
    public void addChild(Result result) {
        if (result == null) {
            return;
        }

        result.setParent(this);

        children.add(result);
    }

    @Override
    public List<Result> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public Result getChild(String name) {
        for (Result result: children) {
            if (result.getName().equals(name)) {
                return result;
            }
        }

        return null;
    }

    @Override
    public void setTime(long time) {
        this.time += time;
        if (getParent() != null) {
            getParent().setTime(time);
        }
    }

    public void setFullPackageName(String fullPackage) {
        this.fullPackageName = fullPackage;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setAllTestCount(int allTestCount) {
        this.allTestCount = allTestCount;
    }

    public int getAllTestCount() {
        return allTestCount;
    }

    public int getPassCount() {
        return getTestCount() - (getFailureCount() + getErrorCount());
    }
}
