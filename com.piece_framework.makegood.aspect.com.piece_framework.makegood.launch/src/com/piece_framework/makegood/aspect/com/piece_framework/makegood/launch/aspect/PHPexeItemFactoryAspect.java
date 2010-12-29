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

package com.piece_framework.makegood.aspect.com.piece_framework.makegood.launch.aspect;

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
public class PHPexeItemFactoryAspect extends Aspect {
    private static final String JOINPOINT_CREATE_SETBODY = "PHPexeItemFactory#create [set body]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_CREATE_SETBODY
    };
    private static final String WEAVINGCLASS_PHPEXEITEMFACTORY =
        "com.piece_framework.makegood.launch.PHPexeItemFactory"; //$NON-NLS-1$
    private static final String[] WEAVINGCLASSES = {
        WEAVINGCLASS_PHPEXEITEMFACTORY
    };

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        Bundle bundle = Platform.getBundle("org.eclipse.php.core"); //$NON-NLS-1$
        Assert.isNotNull(bundle, "No bundle is found for org.eclipse.php.core."); //$NON-NLS-1$
        Assert.isTrue(
            bundle.getVersion().compareTo(Version.parseVersion("2.1.0")) >= 0, //$NON-NLS-1$
            "The version of the bundle org.eclipse.php.core must be greater than or equal to 2.1.0." //$NON-NLS-1$
        );

        CtClass weavingClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPEXEITEMFACTORY);
        CtMethod weavingMethod = weavingClass.getDeclaredMethod("create"); //$NON-NLS-1$
        weavingMethod.setBody(
            bundle.getVersion().compareTo(Version.parseVersion("2.2.0")) >= 0 ? //$NON-NLS-1$
                "return com.piece_framework.makegood.aspect.com.piece_framework.makegood.launch.aspect.HeliosPHPexeItemFactory.create($$);" : //$NON-NLS-1$
                "return com.piece_framework.makegood.aspect.com.piece_framework.makegood.launch.aspect.GalileoPHPexeItemFactory.create($$);" //$NON-NLS-1$
        );
        markJoinPointAsPassed(JOINPOINT_CREATE_SETBODY);
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
