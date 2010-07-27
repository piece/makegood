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

package com.piece_framework.makegood.aspect.program_arguments_parsing;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.piece_framework.makegood.javassist.BundleLoader;
import com.piece_framework.makegood.javassist.CannotWeaveException;
import com.piece_framework.makegood.javassist.WeavingChecker;

public class Startup implements IStartup {
    private static final String PHPLAUNCHUTILITIES_GETPROGRAMARGUMENTS =
        "PHPLaunchUtilities#getProgramArguments()";     //$NON-NLS-1$
    private WeavingChecker checker =
        new WeavingChecker(
            new String[] {PHPLAUNCHUTILITIES_GETPROGRAMARGUMENTS}
        );

    @Override
    public void earlyStartup() {
        Bundle bundle = Platform.getBundle("org.eclipse.php.debug.core");
        Version baseVersion = Version.parseVersion("2.2.0");
        if (bundle.getVersion().compareTo(baseVersion) >= 0) {
            MonitorTarget.endWeaving = true;
            return;
        }

        BundleLoader loader = new BundleLoader(
                new String[]{"org.eclipse.php.debug.core", //$NON-NLS-1$
                             "org.eclipse.debug.core", //$NON-NLS-1$
                             "org.eclipse.core.variables" //$NON-NLS-1$
                             });
        try {
            loader.load();
        } catch (NotFoundException e) {
            log(e);
            return;
        }

        try {
            CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.debug.core.launching.PHPLaunchUtilities"); //$NON-NLS-1$
            modifyGetProgramArguments(targetClass);
            targetClass.toClass(getClass().getClassLoader(), null);
            checker.checkAll();
        } catch (NotFoundException e) {
            log(e);
        } catch (CannotCompileException e) {
            log(e);
        } catch (CannotWeaveException e) {
            log(e);
        }

        MonitorTarget.endWeaving = true;
    }

    private void modifyGetProgramArguments(CtClass targetClass) throws NotFoundException, CannotCompileException {
        CtMethod targetMethod = targetClass.getDeclaredMethod("getProgramArguments"); //$NON-NLS-1$
        targetMethod.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall methodCall) throws CannotCompileException {
                if (methodCall.getMethodName().equals("split")) {
                    methodCall.replace(
"$_ = org.eclipse.debug.core.DebugPlugin.parseArguments($0);" //$NON-NLS-1$
                        );
                    checker.pass(PHPLAUNCHUTILITIES_GETPROGRAMARGUMENTS);
                }
            }
        });
    }

    private void log(Exception e) {
        IStatus status = new Status(IStatus.ERROR,
                                    "com.piece_framework.makegood.aspect.program_arguments_parsing", //$NON-NLS-1$
                                    0,
                                    e.getMessage(),
                                    e
                                    );
        Bundle bundle = Platform.getBundle("com.piece_framework.makegood.aspect.program_arguments_parsing"); //$NON-NLS-1$
        Platform.getLog(bundle).log(status);
    }
}
