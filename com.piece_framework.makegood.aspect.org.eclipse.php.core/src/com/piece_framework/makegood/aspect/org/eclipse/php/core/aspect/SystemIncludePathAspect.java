/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2014 KUBO Atsuhiro <kubo@iteman.jp>,
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
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.Cast;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import com.piece_framework.makegood.aspect.Aspect;

public class SystemIncludePathAspect extends Aspect {
    private static final String JOINPOINT_CAST_ICONTAINER = "PHPSearchEngine#find [cast IContainer]"; //$NON-NLS-1$
    private static final String JOINPOINT_CALL_FINDMEMBER = "PHPSearchEngine#find [call findMember()]"; //$NON-NLS-1$
    private static final String JOINPOINT_CALL_VISITENTRY = "IncludeStatementStrategy#apply [call visitEntry()]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_CAST_ICONTAINER,
        JOINPOINT_CALL_FINDMEMBER,
        JOINPOINT_CALL_VISITENTRY,
    };
    private static final String WEAVINGCLASS_PHPSEARCHENGINE =
        "org.eclipse.php.internal.core.util.PHPSearchEngine"; //$NON-NLS-1$

    /**
     * @since 1.6.0
     */
    private static final String WEAVINGCLASS_INCLUDESTATEMENTSTRATEGY =
        "org.eclipse.php.internal.core.codeassist.strategies.IncludeStatementStrategy"; //$NON-NLS-1$

    private static final String[] WEAVINGCLASSES = {
        WEAVINGCLASS_PHPSEARCHENGINE,
        WEAVINGCLASS_INCLUDESTATEMENTSTRATEGY,
    };

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        weaveIntoPHPSearchEngine();
        weaveIntoIncludeStatementStrategy();
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
    private void weaveIntoPHPSearchEngine() throws NotFoundException, CannotCompileException {
        CtClass weavingClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPSEARCHENGINE);
        CtMethod weavingMethod = weavingClass.getDeclaredMethod("internalFind", new CtClass[] { //$NON-NLS-1$
            ClassPool.getDefault().get("java.lang.String"), //$NON-NLS-1$
            ClassPool.getDefault().get("java.lang.String"), //$NON-NLS-1$
            ClassPool.getDefault().get("java.lang.String"), //$NON-NLS-1$
            ClassPool.getDefault().get("org.eclipse.core.resources.IProject"), //$NON-NLS-1$
            ClassPool.getDefault().get("java.util.Set") //$NON-NLS-1$
        });

        weavingMethod.instrument(
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

                        markJoinPointAsPassed(JOINPOINT_CAST_ICONTAINER);
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

                        markJoinPointAsPassed(JOINPOINT_CALL_FINDMEMBER);
                    }
                }
            }
        );
        markClassAsWoven(weavingClass);
    }

    /**
     * @since 1.6.0
     */
    private void weaveIntoIncludeStatementStrategy() throws NotFoundException, CannotCompileException {
        CtClass weavingClass = ClassPool.getDefault().get(WEAVINGCLASS_INCLUDESTATEMENTSTRATEGY);
        CtMethod weavingMethod = weavingClass.getDeclaredMethod("apply"); //$NON-NLS-1$
        weavingMethod.instrument(new ExprEditor() {
            public void edit(MethodCall methodCall) throws CannotCompileException {
                if (methodCall.getClassName().equals(WEAVINGCLASS_INCLUDESTATEMENTSTRATEGY)
                    && methodCall.getMethodName().equals("visitEntry")) { //$NON-NLS-1$
                            methodCall.replace(
"if (com.piece_framework.makegood.includepath.ConfigurationIncludePath.equalsDummyResource(includePath.getProject(), includePath.getEntry())) {" + //$NON-NLS-1$
"    $_ = null;" + //$NON-NLS-1$
"} else {" + //$NON-NLS-1$
"    $_ = $proceed($$);" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                            );

                            markJoinPointAsPassed(JOINPOINT_CALL_VISITENTRY);
                        }
                    }
                }
        );
        markClassAsWoven(weavingClass);
    }
}
