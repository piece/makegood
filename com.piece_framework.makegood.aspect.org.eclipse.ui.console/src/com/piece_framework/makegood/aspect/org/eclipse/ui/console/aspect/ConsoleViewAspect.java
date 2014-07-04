/**
 * Copyright (c) 2014 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.org.eclipse.ui.console.aspect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import com.piece_framework.makegood.aspect.Aspect;

public class ConsoleViewAspect extends Aspect {
    private static final String JOINPOINT_CALL_ACTIVATE = "IOConsoleOutputStream#notifyParitioner [call activate()]"; //$NON-NLS-1$
    private static final String JOINPOINT_CALL_WARNOFCONTENTCHANGE = "IOConsoleOutputStream#notifyParitioner [call warnOfContentChange()]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_CALL_ACTIVATE,
        JOINPOINT_CALL_WARNOFCONTENTCHANGE,
    };
    private static final String WEAVINGCLASS_IOCONSOLEOUTPUTSTREAM = "org.eclipse.ui.console.IOConsoleOutputStream"; //$NON-NLS-1$

    private static final String[] WEAVINGCLASSES = {
        WEAVINGCLASS_IOCONSOLEOUTPUTSTREAM,
    };

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        weaveIntoIOConsoleOutputStream();
    }

    @Override
    protected String[] joinPoints() {
        return JOINPOINTS;
    }

    @Override
    protected String[] weavingClasses() {
        return WEAVINGCLASSES;
    }

    /**
     * @since 1.6.0
     */
    private void weaveIntoIOConsoleOutputStream() throws NotFoundException, CannotCompileException {
        CtClass weavingClass = ClassPool.getDefault().get(WEAVINGCLASS_IOCONSOLEOUTPUTSTREAM);
        weavingClass.getDeclaredMethod("notifyParitioner").instrument(new ExprEditor() {
            public void edit(MethodCall methodCall) throws CannotCompileException {
                String className = methodCall.getClassName();
                String methodName = methodCall.getMethodName();

                if ("org.eclipse.ui.console.IOConsole".equals(className) && "activate".equals(methodName)) { //$NON-NLS-1$ //$NON-NLS-2$
                    methodCall.replace(
"if (com.piece_framework.makegood.launch.TestLifecycle.isRunning()) {" + //$NON-NLS-1$
"    $_ = null;" + //$NON-NLS-1$
"} else {" + //$NON-NLS-1$
"    $_ = $proceed($$);" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                    );
                    markJoinPointAsPassed(JOINPOINT_CALL_ACTIVATE);

                    return;
                }

                if ("org.eclipse.ui.console.IConsoleManager".equals(className) && "warnOfContentChange".equals(methodName)) { //$NON-NLS-1$ //$NON-NLS-2$
                    methodCall.replace(
"if (com.piece_framework.makegood.launch.TestLifecycle.isRunning()) {" + //$NON-NLS-1$
"    $_ = null;" + //$NON-NLS-1$
"} else {" + //$NON-NLS-1$
"    $_ = $proceed($$);" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                    );
                    markJoinPointAsPassed(JOINPOINT_CALL_WARNOFCONTENTCHANGE);

                    return;
                }
            }
        });
        markClassAsWoven(weavingClass);
    }
}
