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

import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.Cast;
import javassist.expr.ExprEditor;
import javassist.expr.Instanceof;
import javassist.expr.MethodCall;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.piece_framework.makegood.javassist.Aspect;

public class SystemIncludePathAspect extends Aspect {
    private static final String JOINPOINT_CAST_ICONTAINER =
        "PHPINIUtil#createPhpIniByProject() [cast IContainer]"; //$NON-NLS-1$
    private static final String JOINPOINT_INSTANCEOF_ICONTAINER =
        "PHPINIUtil#createPhpIniByProject() [instanceof IContainer]"; //$NON-NLS-1$
    private static final String JOINPOINT_CALL_GETLOCATION =
        "PHPINIUtil#createPhpIniByProject() [call getLocation()]"; //$NON-NLS-1$
    private static final String JOINPOINT_CALL_MODIFYINCLUDEPATH =
        "PHPINIUtil#createPhpIniByProject() [call modifyIncludePath()]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_CAST_ICONTAINER,
        JOINPOINT_INSTANCEOF_ICONTAINER,
        JOINPOINT_CALL_GETLOCATION,
        JOINPOINT_CALL_MODIFYINCLUDEPATH
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
                @Override
                public void edit(Instanceof instance) throws CannotCompileException {
                    CtClass instanceofClass;
                    try {
                        instanceofClass = instance.getType();
                    } catch (NotFoundException e) {
                        return;
                    }

                    if (instanceofClass == null) return;

                    if (instanceofClass.getName().equals("org.eclipse.core.resources.IContainer")) { //$NON-NLS-1$
                        instance.replace(
"$_ = (pathObject.getEntry() instanceof org.eclipse.core.resources.IResource);" //$NON-NLS-1$
                        );

                        markJoinPointAsPassed(JOINPOINT_INSTANCEOF_ICONTAINER);
                    }
                }

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

                        markJoinPointAsPassed(JOINPOINT_CAST_ICONTAINER);
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

                        markJoinPointAsPassed(JOINPOINT_CALL_GETLOCATION);
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

                        markJoinPointAsPassed(JOINPOINT_CALL_MODIFYINCLUDEPATH);
                    }
                }
            }
        );
        markClassAsWoven(weavingClass);
    }

    @Override
    protected String[] joinPoints() {
        Bundle bundle = Platform.getBundle("org.eclipse.php.debug.core"); //$NON-NLS-1$
        if (bundle.getVersion().compareTo(Version.parseVersion("2.2.0.v20100826")) < 0) {
            List<String> joinPoints = new ArrayList<String>();
            for (String joinPoint: JOINPOINTS) {
                if (!joinPoint.equals(JOINPOINT_INSTANCEOF_ICONTAINER)) joinPoints.add(joinPoint);
            }
            return joinPoints.toArray(new String[JOINPOINTS.length - 1]);
        }
        return JOINPOINTS;
    }

    @Override
    protected String[] weavingClasses() {
        return WEAVINGCLASSES;
    }
}
