package com.piece_framework.makegood.ui.views;

import com.piece_framework.makegood.launch.elements.ProblemType;

public class TestProgress {
    private int allTestCount;
    private int endTestCount;
    private boolean initialized;
    private int passCount;
    private int failureCount;
    private int errorCount;

    public void initialize(int allTestCount) {
        this.allTestCount = allTestCount;
        endTestCount = 0;
        passCount = 0;
        failureCount = 0;
        errorCount = 0;

        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public int getAllTestCount() {
        return allTestCount;
    }

    public int getEndTestCount() {
        return endTestCount;
    }

    public void incrementEndTestCount() {
        ++endTestCount;
    }

    public void incrementResultCount(ProblemType problemType) {
        if (problemType == ProblemType.Pass) {
            ++passCount;
        } else if (problemType == ProblemType.Failure) {
            ++failureCount;
        } else if (problemType == ProblemType.Error) {
            ++errorCount;
        }
    }

    public int getPassCount() {
        return passCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public int getErrorCount() {
        return errorCount;
    }
}
