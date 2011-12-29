/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core.result;

import java.util.List;

public abstract class Result {
    private String name;
    private long time;
    private String file;
    private Result parent;

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

    public void setTime(long time) {
        this.time = time;
    }

    public abstract List<Result> getChildren();

    public boolean hasErrors() {
        return getErrorCount() > 0;
    }

    public boolean hasFailures() {
        return getFailureCount() > 0;
    }

    public abstract void addChild(Result result);

    public void setFile(String file) {
        this.file = file;
    }

    public abstract int getTestCount();

    public abstract int getErrorCount();

    public abstract int getFailureCount();

    public abstract boolean isFixed();

    public abstract boolean hasChildren();

    public Result getParent() {
        return parent;
    }

    public abstract int getSize();

    /**
     * @since 1.7.0
     */
    public void setParent(Result parent) {
        this.parent = parent;
    }

    /**
     * @since 1.9.0
     */
    public Result getLast() {
        if (hasChildren()) {
            List<Result> children = getChildren();
            return children.get(children.size() - 1).getLast();
        } else {
            return this;
        }
    }
}
