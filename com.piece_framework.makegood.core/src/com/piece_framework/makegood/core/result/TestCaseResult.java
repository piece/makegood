/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestCaseResult extends Result {
    private String className;
    private int line = 1;
    private boolean isArtificial = false;
    private String failureType;
    private String failureTrace;
    private boolean isFixed = false;
    private ResultType resultType = ResultType.PASS;

    /**
     * @since 1.3.0
     */
    private String failureMessage;

    public TestCaseResult(String name) {
        super(name);
    }

    public String getClassName() {
        return className;
    }

    public int getLine() {
        return line;
    }

    @Override
    public boolean hasErrors() {
        return ResultType.ERROR.equals(resultType);
    }

    @Override
    public boolean hasFailures() {
        return ResultType.FAILURE.equals(resultType);
    }

    @Override
    public List<Result> getChildren() {
        return Collections.unmodifiableList(new ArrayList<Result>());
    }

    @Override
    public void setTime(long time) {
        super.setTime(time);
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

    public void markAsArtificial() {
        this.isArtificial = true;
    }

    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    /**
     * @since 1.2.0
     */
    public String getFailureType() {
        return failureType;
    }

    public void setFailureTrace(String failureTrace) {
        this.failureTrace = failureTrace;
    }

    public String getFailureTrace() {
        return failureTrace;
    }

    @Override
    public int getTestCount() {
        if (!isFixed) return 0;
        return 1;
    }

    @Override
    public int getErrorCount() {
        if (!isFixed) return 0;
        return hasErrors() ? 1: 0;
    }

    @Override
    public int getFailureCount() {
        if (!isFixed) return 0;
        return hasFailures() ? 1 : 0;
    }

    @Override
    public boolean isFixed() {
        return isFixed;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    /**
     * @since 1.2.0
     */
    public ResultType getResultType() {
        return resultType;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public int getSize() {
        return 1;
    }

    /**
     * @since 1.2.0
     */
    public void fix() {
        isFixed = true;
    }

    /**
     * @since 1.3.0
     */
    @Override
    public String getFile() {
        if (super.getFile() == null) {
            return getParent().getFile();
        }
        return super.getFile();
    }

    /**
     * @since 1.3.0
     */
    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    /**
     * @since 1.3.0
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    /**
     * @since 1.7.0
     */
    @Override
    public void addChild(Result result) {
    }
}
