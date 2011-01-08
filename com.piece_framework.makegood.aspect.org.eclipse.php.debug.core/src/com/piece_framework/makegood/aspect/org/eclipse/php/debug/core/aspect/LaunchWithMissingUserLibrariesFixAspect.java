/**
 * Copyright (c) 2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
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

import com.piece_framework.makegood.aspect.Aspect;

public class LaunchWithMissingUserLibrariesFixAspect extends Aspect {
    private static final String JOINPOINT_CALL_GETBUILDPATHENTRIES =
        "PHPINIUtil#createPhpIniByProject() [call getBuildpathEntries()]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_CALL_GETBUILDPATHENTRIES
    };
    private static final String WEAVINGCLASS_PHPINIUTIL =
        "org.eclipse.php.internal.debug.core.phpIni.PHPINIUtil"; //$NON-NLS-1$
    private static final String[] WEAVINGCLASSES = {
        WEAVINGCLASS_PHPINIUTIL
    };

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        CtClass weavingClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPINIUTIL);
        weavingClass.getDeclaredMethod("createPhpIniByProject").instrument( //$NON-NLS-1$
            new ExprEditor() {
                public void edit(MethodCall methodCall) throws CannotCompileException {
                    if (methodCall.getClassName().equals("org.eclipse.dltk.core.IBuildpathContainer") //$NON-NLS-1$
                        && methodCall.getMethodName().equals("getBuildpathEntries")) { //$NON-NLS-1$
                        methodCall.replace(
"$_ = null;" + //$NON-NLS-1$
"if (buildpathContainer != null) {" + //$NON-NLS-1$
"    $_ = $proceed($$);" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                        );

                        markJoinPointAsPassed(JOINPOINT_CALL_GETBUILDPATHENTRIES);
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
