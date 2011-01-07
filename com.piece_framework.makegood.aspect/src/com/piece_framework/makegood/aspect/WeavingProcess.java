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

import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

public abstract class WeavingProcess {
    private Map<CtClass, CtClass> wovenClasses = new IdentityHashMap<CtClass, CtClass>();

    public void process() {
        boolean result = loadDependencies();
        if (!result) return;
        weaveAspects();
        replaceClasses();
    }

    protected abstract String pluginId();

    protected abstract Aspect[] aspects();

    protected abstract String[] dependencies();

    private boolean loadDependencies() {
        try {
            new BundleLoader(dependencies()).load();
        } catch (NotFoundException e) {
            log(e);
            return false;
        }

        return true;
    }

    private void weaveAspects() {
        for (Aspect aspect: aspects()) {
            try {
                aspect.weave();
            } catch (NotFoundException e) {
                log(e);
                continue;
            } catch (CannotCompileException e) {
                log(e);
                continue;
            } catch (CannotWeaveException e) {
                log(e);
                continue;
            }

            for (CtClass wovenClass: aspect.getWovenClasses()) {
                wovenClasses.put(wovenClass, wovenClass);
            }
        }
    }

    private void replaceClasses() {
        for (CtClass wovenClass: wovenClasses.values()) {
            try {
                wovenClass.toClass(getClass().getClassLoader(), null);
            } catch (CannotCompileException e) {
                log(e);
                continue;
            }
        }
    }

    private void log(Exception e) {
        Platform.getLog(Platform.getBundle(pluginId()))
                .log(new Status(IStatus.ERROR, pluginId(), 0, e.getMessage(), e));
    }
}
