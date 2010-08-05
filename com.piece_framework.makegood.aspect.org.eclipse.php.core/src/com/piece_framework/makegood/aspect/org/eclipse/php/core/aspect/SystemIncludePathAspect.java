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

package com.piece_framework.makegood.aspect.org.eclipse.php.core.aspect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.Cast;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.piece_framework.makegood.javassist.Aspect;

public class SystemIncludePathAspect extends Aspect {
    private static final String JOINPOINT_CAST_ICONTAINER = "PHPSearchEngine#find [cast IContainer]"; //$NON-NLS-1$
    private static final String JOINPOINT_CALL_FINDMEMBER = "PHPSearchEngine#find [call findMember()]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_CAST_ICONTAINER,
        JOINPOINT_CALL_FINDMEMBER
    };

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        Bundle bundle = Platform.getBundle("org.eclipse.php.core"); //$NON-NLS-1$
        org.eclipse.core.runtime.Assert.isNotNull(bundle);
        org.eclipse.core.runtime.Assert.isTrue(
            bundle.getVersion().compareTo(Version.parseVersion("2.1.0")) >= 0
        );

        CtClass weavingClass = ClassPool.getDefault().get("org.eclipse.php.internal.core.util.PHPSearchEngine"); //$NON-NLS-1$
        weavingClass.getDeclaredMethod(
            bundle.getVersion().compareTo(Version.parseVersion("2.2.0")) >= 0 ? "internalFind" : "find" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ).instrument( //$NON-NLS-1$
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
"if (includePath.getEntry() instanceof org.eclipse.core.resources.IContainer) {" + //$NON-NLS-1$
"    $_ = ($r) includePath.getEntry();" + //$NON-NLS-1$
"} else {" + //$NON-NLS-1$
    "$_ = null;" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                        );

                        pass(JOINPOINT_CAST_ICONTAINER);
                    }
                }

                public void edit(MethodCall methodCall) throws CannotCompileException {
                    if (methodCall.getClassName().equals("org.eclipse.core.resources.IContainer") //$NON-NLS-1$
                        && methodCall.getMethodName().equals("findMember")) { //$NON-NLS-1$
                        methodCall.replace(
"if (container != null) {" + //$NON-NLS-1$
"    $_ = $proceed($$);" + //$NON-NLS-1$
"} else {" + //$NON-NLS-1$
"    $_ = null;" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                        );

                        pass(JOINPOINT_CALL_FINDMEMBER);
                    }
                }
            }
        );
    }

    @Override
    protected String[] joinPoints() {
        return JOINPOINTS;
    }
}
