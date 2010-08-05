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

package com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.Fragment;
import com.piece_framework.makegood.javassist.Aspect;

public class XdebugConsoleFixAspect extends Aspect {
    private static final String JOINPOINT_NEW_PROCESSCRASHDETECTOR =
        "XDebugExeLaunchConfigurationDelegate#launch() [new ProcessCrashDetector]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_NEW_PROCESSCRASHDETECTOR
    };
    private static final String WEAVINGCLASS_XDEBUGEXELAUNCHCONFIGURATIONDELEGATE =
        "org.eclipse.php.internal.debug.core.launching.XDebugExeLaunchConfigurationDelegate"; //$NON-NLS-1$
    private static final String[] WEAVINGCLASSES = {
        WEAVINGCLASS_XDEBUGEXELAUNCHCONFIGURATIONDELEGATE
    };

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        CtClass weavingClass = ClassPool.getDefault().get(WEAVINGCLASS_XDEBUGEXELAUNCHCONFIGURATIONDELEGATE);
        weavingClass.getDeclaredMethod("launch").instrument( //$NON-NLS-1$
            new ExprEditor() {
                @Override
                public void edit(NewExpr newExpr) throws CannotCompileException {
                    if (newExpr.getClassName().equals("org.eclipse.php.internal.debug.core.zend.debugger.ProcessCrashDetector")) { //$NON-NLS-1$
                        Bundle bundle = Platform.getBundle("org.eclipse.php.debug.core"); //$NON-NLS-1$
                        org.eclipse.core.runtime.Assert.isNotNull(bundle);
                        org.eclipse.core.runtime.Assert.isTrue(
                            bundle.getVersion().compareTo(Version.parseVersion("2.1.0")) >= 0
                        );

                        String className;
                        if (bundle.getVersion().compareTo(Version.parseVersion("2.2.0")) >= 0) { //$NON-NLS-1$
                            className = Fragment.ID + ".aspect.HeliosProcessCrashDetector"; //$NON-NLS-1$
                        } else {
                            className = Fragment.ID + ".aspect.GalileoProcessCrashDetector"; //$NON-NLS-1$
                        }

                        newExpr.replace("$_ = new " + className + "($$);"); //$NON-NLS-1$ //$NON-NLS-2$

                        markJoinPointAsPassed(JOINPOINT_NEW_PROCESSCRASHDETECTOR);
                    }
                }
            }
        );
        markClassAsWoven(weavingClass);
    }

    @Override
    protected String[] joinPoints() {
        return JOINPOINTS;
    }

    @Override
    protected String[] weavingClasses() {
        return WEAVINGCLASSES;
    }
}
