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
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

import com.piece_framework.makegood.javassist.Aspect;
import com.piece_framework.makegood.javassist.PDTVersion;

public class XdebugConsoleFixAspect extends Aspect {
    private static final String JOINPOINT_NEW_PROCESSCRASHDETECTOR =
        "XDebugExeLaunchConfigurationDelegate#launch() [new ProcessCrashDetector]"; //$NON-NLS-1$
    private static final String JOINPOINT_RUN_SETBODY = "ProcessCrashDetector#run [set body]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_NEW_PROCESSCRASHDETECTOR,
        JOINPOINT_RUN_SETBODY
    };
    private static final String WEAVINGCLASS_XDEBUGEXELAUNCHCONFIGURATIONDELEGATE =
        "org.eclipse.php.internal.debug.core.launching.XDebugExeLaunchConfigurationDelegate"; //$NON-NLS-1$
    private static final String WEAVINGCLASS_PROCESSCRASHDETECTOR =
        "com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect.ProcessCrashDetector"; //$NON-NLS-1$
    private static final String[] WEAVINGCLASSES = {
        WEAVINGCLASS_XDEBUGEXELAUNCHCONFIGURATIONDELEGATE,
        WEAVINGCLASS_PROCESSCRASHDETECTOR
    };

    /**
     * @see org.eclipse.php.internal.debug.core.zend.debugger.ProcessCrashDetector#run()
     */
    private static final String PROCESSCRASHDETECTOR_METHOD_RUN_HELIOS =
"{" + //$NON-NLS-1$
"    try {" + //$NON-NLS-1$
"        int exitValue = process.waitFor();" + //$NON-NLS-1$
"        org.eclipse.debug.core.model.IDebugTarget debugTarget = launch.getDebugTarget();" + //$NON-NLS-1$
"        if (debugTarget != null) {" + //$NON-NLS-1$
"            org.eclipse.debug.core.model.IProcess p = debugTarget.getProcess();" + //$NON-NLS-1$
"            if (p instanceof org.eclipse.php.internal.debug.core.launching.PHPProcess) {" + //$NON-NLS-1$
"                ((org.eclipse.php.internal.debug.core.launching.PHPProcess) p).setExitValue(exitValue);" + //$NON-NLS-1$
"            }" + //$NON-NLS-1$
"        }" + //$NON-NLS-1$
"    } catch (Throwable t) {" + //$NON-NLS-1$
"    }" + //$NON-NLS-1$
"}"; //$NON-NLS-1$

    /**
     * @see org.eclipse.php.internal.debug.core.zend.debugger.ProcessCrashDetector#run()
     */
    private static final String PROCESSCRASHDETECTOR_METHOD_RUN_GALILEO =
"{" + //$NON-NLS-1$
"    try {" + //$NON-NLS-1$
"        process.waitFor();" + //$NON-NLS-1$
"    } catch (Throwable t) {" + //$NON-NLS-1$
"    }" + //$NON-NLS-1$
"}"; //$NON-NLS-1$

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        CtClass weavingClass1 = ClassPool.getDefault().get(WEAVINGCLASS_PROCESSCRASHDETECTOR);
        CtMethod weavingMethod1 = weavingClass1.getDeclaredMethod("run"); //$NON-NLS-1$
        weavingMethod1.setBody(
            PDTVersion.getInstance().compareTo("2.2.0") >= 0 ? //$NON-NLS-1$
                PROCESSCRASHDETECTOR_METHOD_RUN_HELIOS :
                PROCESSCRASHDETECTOR_METHOD_RUN_GALILEO
        );
        markJoinPointAsPassed(JOINPOINT_RUN_SETBODY);
        markClassAsWoven(weavingClass1);

        CtClass weavingClass2 = ClassPool.getDefault().get(WEAVINGCLASS_XDEBUGEXELAUNCHCONFIGURATIONDELEGATE);
        weavingClass2.getDeclaredMethod("launch").instrument( //$NON-NLS-1$
            new ExprEditor() {
                @Override
                public void edit(NewExpr newExpr) throws CannotCompileException {
                    if (newExpr.getClassName().equals("org.eclipse.php.internal.debug.core.zend.debugger.ProcessCrashDetector")) { //$NON-NLS-1$
                        newExpr.replace("$_ = new " + WEAVINGCLASS_PROCESSCRASHDETECTOR + "($$);"); //$NON-NLS-1$ //$NON-NLS-2$
                        markJoinPointAsPassed(JOINPOINT_NEW_PROCESSCRASHDETECTOR);
                    }
                }
            }
        );
        markClassAsWoven(weavingClass2);
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
