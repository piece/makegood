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

package com.piece_framework.makegood.aspect;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

public abstract class Aspect {
    private List<CtClass> wovenClasses = new ArrayList<CtClass>();
    private List<String> passedJoinPoints = new ArrayList<String>();

    public void weave() throws NotFoundException, CannotCompileException, CannotWeaveException {
        doWeave();
        verifyWeaving();
    }

    public List<CtClass> getWovenClasses() {
        return wovenClasses;
    }

    protected abstract void doWeave() throws NotFoundException, CannotCompileException;

    protected abstract String[] joinPoints();

    protected void markClassAsWoven(CtClass wovenClass) {
        boolean found = false;
        for (String weavingClass: weavingClasses()) {
            if (wovenClass.getName().equals(weavingClass)) {
                found = true;
            }
        }
        Assert.isTrue(found, "The given class [ " + wovenClass.getName() + " ] is not found in the list by the weavingClasses() method."); //$NON-NLS-1$ //$NON-NLS-2$

        wovenClasses.add(wovenClass);
    }

    protected void markJoinPointAsPassed(String passedJoinPoint) {
        boolean found = false;
        for (String joinPoint: joinPoints()) {
            if (joinPoint.equals(passedJoinPoint)) {
                found = true;
            }
        }
        Assert.isTrue(found, "The given join point [ " + passedJoinPoint + " ] is not found in the list by the joinPoints() method."); //$NON-NLS-1$ //$NON-NLS-2$

        passedJoinPoints.add(passedJoinPoint);
    }

    protected abstract String[] weavingClasses();

    private void verifyWeaving() throws CannotWeaveException {
        verifyJoinPoints();
        verifyWeavingClasses();
    }

    private void verifyJoinPoints() throws CannotWeaveException {
        List<String> failedJoinPoints = new ArrayList<String>();

        for (String joinPoint: joinPoints()) {
            if (!passedJoinPoints.contains(joinPoint)) {
                failedJoinPoints.add(joinPoint);
            }
        }

        if (!failedJoinPoints.isEmpty()) {
            throw new CannotWeaveException("Failed to weave aspects to the following join points: " + failedJoinPoints.toString());
        }
    }

    private void verifyWeavingClasses() {
        for (String weavingClass: weavingClasses()) {
            boolean found = false;
            for (CtClass wovenClass: wovenClasses) {
                if (wovenClass.getName().equals(weavingClass)) {
                    found = true;
                }
            }
            Assert.isTrue(found, "The class [ " + weavingClass + " ] appearing in the class [ " + this.getClass().getName() + " ] have been modified, but not been written."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }
}
