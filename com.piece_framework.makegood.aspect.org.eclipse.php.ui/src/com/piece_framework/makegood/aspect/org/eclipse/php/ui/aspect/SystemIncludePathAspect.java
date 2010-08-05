/**
 * Copyright (c) 2009 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.org.eclipse.php.ui.aspect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.piece_framework.makegood.javassist.Aspect;

public class SystemIncludePathAspect extends Aspect {
    private static final String JOINPOINT_GETCPLISTELEMENTTEXT_INSERTBEFORE =
        "PHPIPListLabelProvider#getCPListElementText() [insert before]"; //$NON-NLS-1$
    private static final String JOINPOINT_GETCPLISTELEMENTTEXT_ADDMETHOD =
        "PHPIPListLabelProvider#getCPListElementText() [add method]"; //$NON-NLS-1$
    private static final String JOINPOINT_GETCPLISTELEMENTBASEIMAGE_INSERTBEFORE =
        "PHPIPListLabelProvider#getCPListElementBaseImage() [insert before]"; //$NON-NLS-1$
    private static final String JOINPOINT_CREATECONTROL_NEW_PHPINCLUDEPATHSOURCEPAGE =
        "PHPIncludePathsBlock#createControl() [new PHPIncludePathSourcePage]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_GETCPLISTELEMENTTEXT_INSERTBEFORE,
        JOINPOINT_GETCPLISTELEMENTTEXT_ADDMETHOD,
        JOINPOINT_GETCPLISTELEMENTBASEIMAGE_INSERTBEFORE,
        JOINPOINT_CREATECONTROL_NEW_PHPINCLUDEPATHSOURCEPAGE
    };
    private static final String WEAVINGCLASS_PHPIPLISTLABELPROVIDER =
        "org.eclipse.php.internal.ui.preferences.includepath.PHPIPListLabelProvider"; //$NON-NLS-1$
    private static final String WEAVINGCLASS_PHPINCLUDEPATHSBLOCK =
        "org.eclipse.php.internal.ui.preferences.includepath.PHPIncludePathsBlock"; //$NON-NLS-1$
    private static final String[] WEAVINGCLASSES = {
        WEAVINGCLASS_PHPIPLISTLABELPROVIDER,
        WEAVINGCLASS_PHPINCLUDEPATHSBLOCK
    };

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        Bundle bundle = Platform.getBundle("org.eclipse.php.ui"); //$NON-NLS-1$
        org.eclipse.core.runtime.Assert.isNotNull(bundle);
        org.eclipse.core.runtime.Assert.isTrue(
            bundle.getVersion().compareTo(Version.parseVersion("2.1.0")) >= 0
        );

        CtClass weavingClass1 = ClassPool.getDefault().get(WEAVINGCLASS_PHPIPLISTLABELPROVIDER);
        if (bundle.getVersion().compareTo(Version.parseVersion("2.2.0")) >= 0) { //$NON-NLS-1$
            editGetCPListElementTextMethod(weavingClass1);
        } else {
            addGetCPListElementTextMethod(weavingClass1);
        }
        editGetCPListElementBaseImageMethod(weavingClass1);
        markClassAsWoven(weavingClass1);

        CtClass weavingClass2 = ClassPool.getDefault().get(WEAVINGCLASS_PHPINCLUDEPATHSBLOCK);
        editCreateControlMethod(weavingClass2);
        markClassAsWoven(weavingClass2);
    }

    private void editGetCPListElementTextMethod(CtClass weavingClass) throws CannotCompileException, NotFoundException {
        weavingClass.getDeclaredMethod("getCPListElementText").insertBefore( //$NON-NLS-1$
"org.eclipse.core.resources.IResource target = cpentry.getResource();" + //$NON-NLS-1$
"if (target != null) {" + //$NON-NLS-1$
"    com.piece_framework.makegood.include_path.ConfigurationIncludePath configuration = new com.piece_framework.makegood.include_path.ConfigurationIncludePath(target.getProject());" + //$NON-NLS-1$
"    if (configuration.equalsDummyResource(target)) {" + //$NON-NLS-1$
"        return com.piece_framework.makegood.include_path.ConfigurationIncludePath.text;" + //$NON-NLS-1$
"    }" + //$NON-NLS-1$
"}" //$NON-NLS-1$
        );

        markJoinPointAsPassed(JOINPOINT_GETCPLISTELEMENTTEXT_INSERTBEFORE);
        markJoinPointAsPassed(JOINPOINT_GETCPLISTELEMENTTEXT_ADDMETHOD);
    }

    private void addGetCPListElementTextMethod(CtClass weavingClass) throws CannotCompileException {
        weavingClass.addMethod(
            CtNewMethod.make(
"public String getCPListElementText(org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement cpentry) {" + //$NON-NLS-1$
"    org.eclipse.core.resources.IResource target = cpentry.getResource();" + //$NON-NLS-1$
"    if (target == null) {" + //$NON-NLS-1$
"        return super.getCPListElementText(cpentry);" + //$NON-NLS-1$
"    }" + //$NON-NLS-1$
"    com.piece_framework.makegood.include_path.ConfigurationIncludePath configuration = new com.piece_framework.makegood.include_path.ConfigurationIncludePath(target.getProject());" + //$NON-NLS-1$
"    if (configuration.equalsDummyResource(target)) {" + //$NON-NLS-1$
"        return com.piece_framework.makegood.include_path.ConfigurationIncludePath.text;" + //$NON-NLS-1$
"    }" + //$NON-NLS-1$
"" + //$NON-NLS-1$
"    return super.getCPListElementText(cpentry);" + //$NON-NLS-1$
"}", //$NON-NLS-1$
                weavingClass
            )
        );

        markJoinPointAsPassed(JOINPOINT_GETCPLISTELEMENTTEXT_ADDMETHOD);
        markJoinPointAsPassed(JOINPOINT_GETCPLISTELEMENTTEXT_INSERTBEFORE);
    }

    private void editGetCPListElementBaseImageMethod(CtClass weavingClass) throws CannotCompileException, NotFoundException {
        weavingClass.getDeclaredMethod("getCPListElementBaseImage").insertBefore( //$NON-NLS-1$
"org.eclipse.core.resources.IResource target = cpentry.getResource();" + //$NON-NLS-1$
"if (target != null) {" + //$NON-NLS-1$
"    com.piece_framework.makegood.include_path.ConfigurationIncludePath configuration = new com.piece_framework.makegood.include_path.ConfigurationIncludePath(target.getProject());" + //$NON-NLS-1$
"    if (configuration.equalsDummyResource(target)) {" + //$NON-NLS-1$
"        return com.piece_framework.makegood.include_path.ConfigurationIncludePath.icon;" + //$NON-NLS-1$
"    }" + //$NON-NLS-1$
"}" //$NON-NLS-1$
        );

        markJoinPointAsPassed(JOINPOINT_GETCPLISTELEMENTBASEIMAGE_INSERTBEFORE);
    }

    private void editCreateControlMethod(CtClass weavingClass) throws NotFoundException, CannotCompileException {
        weavingClass.getDeclaredMethod("createControl").instrument( //$NON-NLS-1$
            new ExprEditor() {
                @Override
                public void edit(NewExpr expression) throws CannotCompileException {
                    if (expression.getClassName().equals("org.eclipse.php.internal.ui.preferences.includepath.PHPIncludePathSourcePage")) { //$NON-NLS-1$
                        expression.replace(
"$_ = new com.piece_framework.makegood.aspect.org.eclipse.php.ui.aspect.PHPIncludePathSourcePageForConfiguration($1);" //$NON-NLS-1$
                        );

                        markJoinPointAsPassed(JOINPOINT_CREATECONTROL_NEW_PHPINCLUDEPATHSOURCEPAGE);
                    }
                }
            }
        );
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
