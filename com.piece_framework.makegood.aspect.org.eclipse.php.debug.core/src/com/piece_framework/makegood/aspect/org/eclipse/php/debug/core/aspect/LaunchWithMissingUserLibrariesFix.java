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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.piece_framework.makegood.javassist.Aspect;

public class LaunchWithMissingUserLibrariesFix extends Aspect {
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
        Bundle bundle = Platform.getBundle("org.eclipse.php.debug.core"); //$NON-NLS-1$
        Assert.isNotNull(bundle, "No bundle is found for org.eclipse.php.debug.core."); //$NON-NLS-1$
        Assert.isTrue(
            bundle.getVersion().compareTo(Version.parseVersion("2.1.0")) >= 0,
            "The version of the bundle org.eclipse.php.debug.core must be greater than or equal to 2.1.0." //$NON-NLS-1$
        );

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
