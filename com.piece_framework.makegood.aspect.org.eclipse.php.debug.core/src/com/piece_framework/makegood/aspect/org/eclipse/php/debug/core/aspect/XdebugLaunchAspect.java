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
import javassist.expr.MethodCall;

import com.piece_framework.makegood.javassist.Aspect;

public class XdebugLaunchAspect extends Aspect {
    private static final String JOINPOINT_CALL_GETLOCATION =
        "XDebugExeLaunchConfigurationDelegate#launch() [call getLocation()]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_CALL_GETLOCATION
    };

    protected void doWeave() throws NotFoundException, CannotCompileException {
        CtClass weavingClass = ClassPool.getDefault().get("org.eclipse.php.internal.debug.core.launching.XDebugExeLaunchConfigurationDelegate"); //$NON-NLS-1$
        weavingClass.getDeclaredMethod("launch").instrument( //$NON-NLS-1$
            new ExprEditor() {
                @Override
                public void edit(MethodCall methodCall) throws CannotCompileException {
                    if (methodCall.getClassName().equals("org.eclipse.core.resources.IResource") //$NON-NLS-1$
                        && methodCall.getMethodName().equals("getLocation")) { //$NON-NLS-1$
                        methodCall.replace(
"if (launch instanceof com.piece_framework.makegood.launch.MakeGoodLaunch) {" + //$NON-NLS-1$
"    $_ = new org.eclipse.core.runtime.Path(com.piece_framework.makegood.launch.MakeGoodLaunchConfigurationDelegate.getCommandPath());" + //$NON-NLS-1$
"} else {" + //$NON-NLS-1$
"    $_ = $proceed($$);" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                        );

                        pass(JOINPOINT_CALL_GETLOCATION);
                    }
                }
            }
        );
        addWeavedClass(weavingClass);
    }

    @Override
    protected String[] joinPoints() {
        return JOINPOINTS;
    }
}
