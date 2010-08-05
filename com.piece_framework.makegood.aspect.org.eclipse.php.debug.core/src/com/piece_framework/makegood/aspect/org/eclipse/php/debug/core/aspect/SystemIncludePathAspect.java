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

package com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.Cast;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import com.piece_framework.makegood.javassist.Aspect;

public class SystemIncludePathAspect extends Aspect {
    private static final String JOINPOINT_CAST_ICONTAINER =
        "PHPINIUtil#createPhpIniByProject() [cast IContainer]"; //$NON-NLS-1$
    private static final String JOINPOINT_CALL_GETLOCATION =
        "PHPINIUtil#createPhpIniByProject() [call getLocation()]"; //$NON-NLS-1$
    private static final String JOINPOINT_CALL_MODIFYINCLUDEPATH =
        "PHPINIUtil#createPhpIniByProject() [call modifyIncludePath()]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_CAST_ICONTAINER,
        JOINPOINT_CALL_GETLOCATION,
        JOINPOINT_CALL_MODIFYINCLUDEPATH
    };

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        CtClass weavingClass = ClassPool.getDefault().get("org.eclipse.php.internal.debug.core.phpIni.PHPINIUtil"); //$NON-NLS-1$
        weavingClass.getDeclaredMethod("createPhpIniByProject").instrument( //$NON-NLS-1$
            new ExprEditor() {
                @Override
                public void edit(Cast cast) throws CannotCompileException {
                    CtClass castClass;
                    try {
                        castClass = cast.getType();
                    } catch (NotFoundException e) {
                        return;
                    }

                    if (castClass == null) return;

                    if (castClass.getName().equals("org.eclipse.core.resources.IContainer")) { //$NON-NLS-1$
                        cast.replace(
"$_ = null;" + //$NON-NLS-1$
"if (pathObject.getEntry() instanceof org.eclipse.core.resources.IContainer) {" + //$NON-NLS-1$
"    $_ = ($r) pathObject.getEntry();" + //$NON-NLS-1$
"} else {" + //$NON-NLS-1$
"    org.eclipse.core.resources.IResource resource = (org.eclipse.core.resources.IResource) pathObject.getEntry();" + //$NON-NLS-1$
"    includePath.add(resource.getFullPath().toOSString());" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                        );

                        pass(JOINPOINT_CAST_ICONTAINER);
                    }
                }

                public void edit(MethodCall methodCall) throws CannotCompileException {
                    if (methodCall.getClassName().equals("org.eclipse.core.resources.IContainer") //$NON-NLS-1$
                        && methodCall.getMethodName().equals("getLocation")) { //$NON-NLS-1$
                        methodCall.replace(
"$_ = null;" + //$NON-NLS-1$
"if (container != null) {" + //$NON-NLS-1$
"    $_ = $proceed($$);" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                        );

                        pass(JOINPOINT_CALL_GETLOCATION);
                    } else if (methodCall.getMethodName().equals("modifyIncludePath")) { //$NON-NLS-1$
                        methodCall.replace(
"com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect.PHPConfiguration phpConfiguration =" + //$NON-NLS-1$
"    new com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect.PHPConfiguration();" + //$NON-NLS-1$
"String[] transformedIncludePaths =" + //$NON-NLS-1$
"    phpConfiguration.transformIncludePaths(" + //$NON-NLS-1$
"        $1," + //$NON-NLS-1$
"        includePath,"+ //$NON-NLS-1$
"        project" + //$NON-NLS-1$
"    );" + //$NON-NLS-1$
"$_ = $proceed($1, transformedIncludePaths);" //$NON-NLS-1$
                        );

                        pass(JOINPOINT_CALL_MODIFYINCLUDEPATH);
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
