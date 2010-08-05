/**
 * Copyright (c) 2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.javassist;

import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

public abstract class Aspect {
    private List<CtClass> weavedClasses = new ArrayList<CtClass>();
    private List<String> passedJoinPoints = new ArrayList<String>();

    public void weave() throws NotFoundException, CannotCompileException, CannotWeaveException {
        doWeave();
        verifyWeaving();
    }

    public List<CtClass> getWeavedClasses() {
        return weavedClasses;
    }

    protected abstract void doWeave() throws NotFoundException, CannotCompileException;

    protected abstract String[] joinPoints();

    protected void addWeavedClass(CtClass weavedClass) {
        weavedClasses.add(weavedClass);
    }

    protected void pass(String joinPointId) {
        passedJoinPoints.add(joinPointId);
    }

    private void verifyWeaving() throws CannotWeaveException {
        List<String> failedJoinPoints = new ArrayList<String>();

        for (String joinPoint: joinPoints()) {
            if (!passedJoinPoints.contains(joinPoint)) {
                failedJoinPoints.add(joinPoint);
            }
        }

        if (!failedJoinPoints.isEmpty()) {
            throw new CannotWeaveException(failedJoinPoints.toString());
        }
    }
}
