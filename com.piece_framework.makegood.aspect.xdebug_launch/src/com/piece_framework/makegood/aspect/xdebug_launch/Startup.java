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

package com.piece_framework.makegood.aspect.xdebug_launch;

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

import com.piece_framework.makegood.javassist.BundleLoader;
import com.piece_framework.makegood.javassist.CannotWeaveException;
import com.piece_framework.makegood.javassist.WeavingChecker;

public class Startup implements IStartup {
    private static final String XDEBUGEXELAUNCHCONFIGURATIONDELEGATE_LAUNCH_CALL_GETLOCATION =
        "XDebugExeLaunchConfigurationDelegate#launch() [call getLocation()]";     //$NON-NLS-1$
    private WeavingChecker checker =
        new WeavingChecker(
            new String[] {XDEBUGEXELAUNCHCONFIGURATIONDELEGATE_LAUNCH_CALL_GETLOCATION}
        );

    @Override
    public void earlyStartup() {
        BundleLoader loader =
            new BundleLoader(
                new String[]{
                    "com.piece_framework.makegood.launch" //$NON-NLS-1$
                }
            );
        try {
            loader.load();
        } catch (NotFoundException e) {
            log(e);
            return;
        }

        try {
            fixLaunchToUseTestRunnerCommandAsPHPFile();
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

    private void fixLaunchToUseTestRunnerCommandAsPHPFile() throws NotFoundException, CannotCompileException {
        CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.debug.core.launching.XDebugExeLaunchConfigurationDelegate"); //$NON-NLS-1$
        CtMethod targetMethod = targetClass.getDeclaredMethod("launch"); //$NON-NLS-1$
        targetMethod.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals("org.eclipse.core.resources.IResource") //$NON-NLS-1$
                    && m.getMethodName().equals("getLocation")) { //$NON-NLS-1$
                    m.replace(
"$_ = new org.eclipse.core.runtime.Path(com.piece_framework.makegood.launch.MakeGoodLaunchConfigurationDelegate.getCommandPath());" //$NON-NLS-1$
                    );
                    checker.pass(XDEBUGEXELAUNCHCONFIGURATIONDELEGATE_LAUNCH_CALL_GETLOCATION);
                }
            }
        });

        targetClass.toClass(getClass().getClassLoader(), null);
    }

    private void log(Exception e) {
        IStatus status = new Status(IStatus.ERROR, "com.piece_framework.makegood.aspect.xdebug_launch", 0, e.getMessage(), e); //$NON-NLS-1$
        Bundle bundle = Platform.getBundle("com.piece_framework.makegood.aspect.xdebug_launch"); //$NON-NLS-1$
        Platform.getLog(bundle).log(status);
    }
}
