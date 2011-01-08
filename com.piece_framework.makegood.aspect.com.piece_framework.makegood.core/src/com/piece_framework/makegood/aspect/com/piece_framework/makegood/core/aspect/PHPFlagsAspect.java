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

package com.piece_framework.makegood.aspect.com.piece_framework.makegood.core.aspect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import com.piece_framework.makegood.aspect.Aspect;
import com.piece_framework.makegood.aspect.PDTVersion;

/**
 * @since 1.2.0
 */
public class PHPFlagsAspect extends Aspect {
    private static final String JOINPOINT_ISCLASS_SETBODY = "PHPFlags#isClass [set body]"; //$NON-NLS-1$
    private static final String JOINPOINT_ISNAMESPACE_SETBODY = "PHPFlags#isNamespace [set body]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_ISCLASS_SETBODY,
        JOINPOINT_ISNAMESPACE_SETBODY
    };
    private static final String WEAVINGCLASS_PHPFLAGS =
        "com.piece_framework.makegood.core.PHPFlags"; //$NON-NLS-1$
    private static final String[] WEAVINGCLASSES = {
        WEAVINGCLASS_PHPFLAGS
    };

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        CtClass weavingClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPFLAGS);

        CtMethod weavingMethod1 = weavingClass.getDeclaredMethod("isClass"); //$NON-NLS-1$
        weavingMethod1.setBody(
            PDTVersion.getInstance().compareTo("2.2.0") >= 0 ? //$NON-NLS-1$
                "return org.eclipse.php.core.compiler.PHPFlags.isClass($$);" : //$NON-NLS-1$
                "return org.eclipse.php.internal.core.compiler.PHPFlags.isClass($$);" //$NON-NLS-1$
        );
        markJoinPointAsPassed(JOINPOINT_ISCLASS_SETBODY);

        CtMethod weavingMethod2 = weavingClass.getDeclaredMethod("isNamespace"); //$NON-NLS-1$
        weavingMethod2.setBody(
            PDTVersion.getInstance().compareTo("2.2.0") >= 0 ? //$NON-NLS-1$
                "return org.eclipse.php.core.compiler.PHPFlags.isNamespace($$);" : //$NON-NLS-1$
                "return org.eclipse.php.internal.core.compiler.PHPFlags.isNamespace($$);" //$NON-NLS-1$
        );
        markJoinPointAsPassed(JOINPOINT_ISNAMESPACE_SETBODY);

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
