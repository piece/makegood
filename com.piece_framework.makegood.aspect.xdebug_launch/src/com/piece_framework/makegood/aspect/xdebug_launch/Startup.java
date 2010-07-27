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
import javassist.expr.NewExpr;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.piece_framework.makegood.javassist.BundleLoader;
import com.piece_framework.makegood.javassist.CannotWeaveException;
import com.piece_framework.makegood.javassist.WeavingChecker;

public class Startup implements IStartup {
    private static final String PLUGIN_ID = "com.piece_framework.makegood.aspect.xdebug_launch"; //$NON-NLS-1$
    private static final String XDEBUGEXELAUNCHCONFIGURATIONDELEGATE_LAUNCH_CALL_GETLOCATION =
        "XDebugExeLaunchConfigurationDelegate#launch() [call getLocation()]"; //$NON-NLS-1$
    private static final String XDEBUGEXELAUNCHCONFIGURATIONDELEGATE_LAUNCH_NEW_PROCESSCRASHDETECTOR =
        "XDebugExeLaunchConfigurationDelegate#launch() [new ProcessCrashDetector]"; //$NON-NLS-1$
    private WeavingChecker checker =
        new WeavingChecker(
            new String[] {
                XDEBUGEXELAUNCHCONFIGURATIONDELEGATE_LAUNCH_CALL_GETLOCATION,
                XDEBUGEXELAUNCHCONFIGURATIONDELEGATE_LAUNCH_NEW_PROCESSCRASHDETECTOR
            }
        );

    @Override
    public void earlyStartup() {
        BundleLoader loader =
            new BundleLoader(
                new String[]{
                    "com.piece_framework.makegood.launch", //$NON-NLS-1$
                    PLUGIN_ID
                }
            );
        try {
            loader.load();
        } catch (NotFoundException e) {
            log(e);
            return;
        }

        try {
            CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.debug.core.launching.XDebugExeLaunchConfigurationDelegate"); //$NON-NLS-1$

            fixLaunchToUseTestRunnerCommandAsPHPFile(targetClass);
            fixLaunchToOutputContentsProperlyToConsoleView(targetClass);

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

    private void fixLaunchToUseTestRunnerCommandAsPHPFile(CtClass targetClass) throws NotFoundException, CannotCompileException {
        CtMethod targetMethod = targetClass.getDeclaredMethod("launch"); //$NON-NLS-1$
        targetMethod.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals("org.eclipse.core.resources.IResource") //$NON-NLS-1$
                    && m.getMethodName().equals("getLocation")) { //$NON-NLS-1$
                    m.replace(
"if (launch instanceof com.piece_framework.makegood.launch.MakeGoodLaunch) {" + //$NON-NLS-1$
"    $_ = new org.eclipse.core.runtime.Path(com.piece_framework.makegood.launch.MakeGoodLaunchConfigurationDelegate.getCommandPath());" + //$NON-NLS-1$
"} else {" + //$NON-NLS-1$
"    $_ = $proceed($$);" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                    );
                    checker.pass(XDEBUGEXELAUNCHCONFIGURATIONDELEGATE_LAUNCH_CALL_GETLOCATION);
                }
            }
        });
    }

    private void fixLaunchToOutputContentsProperlyToConsoleView(CtClass targetClass) throws NotFoundException, CannotCompileException {
        CtMethod targetMethod = targetClass.getDeclaredMethod("launch"); //$NON-NLS-1$
        targetMethod.instrument(new ExprEditor() {
            @Override
            public void edit(NewExpr newExpr) throws CannotCompileException {
                if (newExpr.getClassName().equals("org.eclipse.php.internal.debug.core.zend.debugger.ProcessCrashDetector")) { //$NON-NLS-1$
                    BundleDescription bundle = Platform.getPlatformAdmin().getState().getBundle(PHPDebugPlugin.ID, null);
                    if (bundle == null) {
                        throw new CannotCompileException("The bundle " + PHPDebugPlugin.ID + " is not found."); //$NON-NLS-1$ //$NON-NLS-2$
                    }

                    Version version = bundle.getVersion();
                    if (version.getMajor() < 2) {
                        throw new CannotCompileException("The version of the bundle " + PHPDebugPlugin.ID + " must be >= 2. The current version is " + version + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }

                    String className;
                    if (version.getMinor() >= 2) {
                        className = PLUGIN_ID + ".helios.ProcessCrashDetector"; //$NON-NLS-1$
                    } else {
                        className = PLUGIN_ID + ".galileo.ProcessCrashDetector"; //$NON-NLS-1$
                    }

                    newExpr.replace("$_ = new " + className + "($$);"); //$NON-NLS-1$ //$NON-NLS-2$
                    checker.pass(XDEBUGEXELAUNCHCONFIGURATIONDELEGATE_LAUNCH_NEW_PROCESSCRASHDETECTOR);
                }
            }
        });
    }

    private void log(Exception e) {
        IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, 0, e.getMessage(), e);
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        Platform.getLog(bundle).log(status);
    }
}
