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

package com.piece_framework.makegood.aspect.com.piece_framework.makegood.core.aspect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.piece_framework.makegood.javassist.Aspect;

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
        Bundle bundle = Platform.getBundle("org.eclipse.php.core"); //$NON-NLS-1$
        Assert.isNotNull(bundle, "No bundle is found for org.eclipse.php.core."); //$NON-NLS-1$
        Assert.isTrue(
            bundle.getVersion().compareTo(Version.parseVersion("2.1.0")) >= 0, //$NON-NLS-1$
            "The version of the bundle org.eclipse.php.core must be greater than or equal to 2.1.0." //$NON-NLS-1$
        );

        CtClass weavingClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPFLAGS);

        CtMethod weavingMethod1 = weavingClass.getDeclaredMethod("isClass"); //$NON-NLS-1$
        weavingMethod1.setBody(
            bundle.getVersion().compareTo(Version.parseVersion("2.2.0")) >= 0 ? //$NON-NLS-1$
                "return org.eclipse.php.core.compiler.PHPFlags.isClass($$);" : //$NON-NLS-1$
                "return org.eclipse.php.internal.core.compiler.PHPFlags.isClass($$);" //$NON-NLS-1$
        );
        markJoinPointAsPassed(JOINPOINT_ISCLASS_SETBODY);

        CtMethod weavingMethod2 = weavingClass.getDeclaredMethod("isNamespace"); //$NON-NLS-1$
        weavingMethod2.setBody(
            bundle.getVersion().compareTo(Version.parseVersion("2.2.0")) >= 0 ? //$NON-NLS-1$
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
