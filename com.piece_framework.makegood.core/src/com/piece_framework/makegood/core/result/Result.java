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
    long time;
    private String file;
    public Result parent;

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

    public void setFile(String file) {
        this.file = file;
    }

    public abstract int getTestCount();

    public abstract int getErrorCount();

    public abstract int getFailureCount();

    public abstract boolean fixed();

    public abstract boolean hasChildren();

    public Result getParent() {
        return parent;
    }

    public abstract int getSize();
}
