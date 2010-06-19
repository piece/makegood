/**
 * Copyright (c) 2009 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.include_path_settings;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.piece_framework.makegood.javassist.BundleLoader;


public class Startup implements IStartup {
    @Override
    public void earlyStartup() {
        BundleLoader loader = new BundleLoader(
                new String[]{"org.eclipse.php.ui", //$NON-NLS-1$
                             "com.piece_framework.makegood.aspect.include_path_settings", //$NON-NLS-1$
                             "com.piece_framework.makegood.include_path", //$NON-NLS-1$
                             "org.eclipse.dltk.ui", //$NON-NLS-1$
                             "org.eclipse.core.resources", //$NON-NLS-1$
                             "org.eclipse.jface" //$NON-NLS-1$
                             });
        try {
            loader.load();
        } catch (NotFoundException e) {
            log(e);
            return;
        }

        try {
            Bundle bundle = Platform.getBundle("org.eclipse.php.ui");
            Version baseVersion = Version.parseVersion("2.2.0");

            CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.ui.preferences.includepath.PHPIPListLabelProvider"); //$NON-NLS-1$
            if (bundle.getVersion().compareTo(baseVersion) >= 0) {
                modifyGetCPListElementTextMethod(targetClass);
            } else {
                addGetCPListElementTextMethod(targetClass);
            }
            modifyGetCPListElementBaseImage(targetClass);
            targetClass.toClass(getClass().getClassLoader(), null);
        } catch (NotFoundException e) {
            log(e);
        } catch (CannotCompileException e) {
            log(e);
        }

        try {
            CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.ui.preferences.includepath.PHPIncludePathsBlock"); //$NON-NLS-1$
            modifyCreateControlMethod(targetClass);
            targetClass.toClass(getClass().getClassLoader(), null);
        } catch (NotFoundException e) {
            log(e);
        } catch (CannotCompileException e) {
            log(e);
        }

        MonitorTarget.endWeaving = true;
    }

    private void addGetCPListElementTextMethod(CtClass targetClass) throws CannotCompileException {
        CtMethod newMethod = CtNewMethod.make(
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
"}" //$NON-NLS-1$
            ,targetClass);
        targetClass.addMethod(newMethod);
    }

    private void modifyGetCPListElementTextMethod(CtClass targetClass) throws NotFoundException, CannotCompileException {
        CtMethod targetMethod = targetClass.getDeclaredMethod("getCPListElementText"); //$NON-NLS-1$
        targetMethod.insertBefore(
"org.eclipse.core.resources.IResource target = cpentry.getResource();" + //$NON-NLS-1$
"if (target != null) {" + //$NON-NLS-1$
"    com.piece_framework.makegood.include_path.ConfigurationIncludePath configuration = new com.piece_framework.makegood.include_path.ConfigurationIncludePath(target.getProject());" + //$NON-NLS-1$
"    if (configuration.equalsDummyResource(target)) {" + //$NON-NLS-1$
"        return com.piece_framework.makegood.include_path.ConfigurationIncludePath.text;" + //$NON-NLS-1$
"    }" + //$NON-NLS-1$
"}" //$NON-NLS-1$
            );
    }

    private void modifyGetCPListElementBaseImage(CtClass targetClass) throws NotFoundException, CannotCompileException {
        CtMethod targetMethod = targetClass.getDeclaredMethod("getCPListElementBaseImage"); //$NON-NLS-1$
        targetMethod.insertBefore(
"org.eclipse.core.resources.IResource target = cpentry.getResource();" + //$NON-NLS-1$
"if (target != null) {" + //$NON-NLS-1$
"    com.piece_framework.makegood.include_path.ConfigurationIncludePath configuration = new com.piece_framework.makegood.include_path.ConfigurationIncludePath(target.getProject());" + //$NON-NLS-1$
"    if (configuration.equalsDummyResource(target)) {" + //$NON-NLS-1$
"        return com.piece_framework.makegood.include_path.ConfigurationIncludePath.icon;" + //$NON-NLS-1$
"    }" + //$NON-NLS-1$
"}" //$NON-NLS-1$
            );
    }

    private void modifyCreateControlMethod(CtClass targetClass) throws NotFoundException, CannotCompileException {
        CtMethod targetMethod = targetClass.getDeclaredMethod("createControl"); //$NON-NLS-1$
        targetMethod.instrument(new ExprEditor() {
            public void edit(NewExpr expression) throws CannotCompileException {
                if (expression.getClassName().equals("org.eclipse.php.internal.ui.preferences.includepath.PHPIncludePathSourcePage")) { //$NON-NLS-1$
                    expression.replace(
"$_ = new com.piece_framework.makegood.aspect.include_path_settings.PHPIncludePathSourcePageForConfiguration($1);" //$NON-NLS-1$
                        );
                }
            }
        });
    }

    private void log(Exception e) {
        IStatus status = new Status(IStatus.ERROR,
                                    "com.piece_framework.makegood.aspect.include_path_settings", //$NON-NLS-1$
                                    0,
                                    e.getMessage(),
                                    e
                                    );
        Bundle bundle = Platform.getBundle("com.piece_framework.makegood.aspect.include_path_settings"); //$NON-NLS-1$
        Platform.getLog(bundle).log(status);
    }
}
