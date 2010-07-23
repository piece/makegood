/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.include_path_protection;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.Cast;
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
    private static final String PHPINIUTIL_CREATEPHPINIBYPROJECT_CAST_ICONTAINER =
        "PHPINIUtil#createPhpIniByProject() [cast IContainer]";     //$NON-NLS-1$
    private static final String PHPINIUTIL_CREATEPHPINIBYPROJECT_CALL_GETLOCATION =
        "PHPINIUtil#createPhpIniByProject() [call getLocation()]";     //$NON-NLS-1$
    private static final String PHPINIUTIL_CREATEPHPINIBYPROJECT_CALL_MODIFYINCLUDEPATH =
        "PHPINIUtil#createPhpIniByProject() [call modifyIncludePath()]";     //$NON-NLS-1$
    private WeavingChecker checker =
        new WeavingChecker(
            new String[] {
                PHPINIUTIL_CREATEPHPINIBYPROJECT_CAST_ICONTAINER,
                PHPINIUTIL_CREATEPHPINIBYPROJECT_CALL_GETLOCATION,
                PHPINIUTIL_CREATEPHPINIBYPROJECT_CALL_MODIFYINCLUDEPATH
            }
        );

    @Override
    public void earlyStartup() {
        BundleLoader loader = new BundleLoader(
                new String[]{"org.eclipse.php.debug.core", //$NON-NLS-1$
                             "com.piece_framework.makegood.aspect.include_path_protection", //$NON-NLS-1$
                             "org.eclipse.core.resources", //$NON-NLS-1$
                             "org.eclipse.equinox.common", //$NON-NLS-1$
                             "org.eclipse.php.core" //$NON-NLS-1$
                             });
        try {
            loader.load();
        } catch (NotFoundException e) {
            log(e);
            return;
        }

        try {
            CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.debug.core.phpIni.PHPINIUtil"); //$NON-NLS-1$
            modifyCreatePhpIniByProject(targetClass);
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

    private void modifyCreatePhpIniByProject(CtClass targetClass) throws NotFoundException, CannotCompileException {
        CtMethod targetMethod = targetClass.getDeclaredMethod("createPhpIniByProject"); //$NON-NLS-1$
        targetMethod.instrument(new ExprEditor() {
            public void edit(Cast cast) throws CannotCompileException {
                CtClass castClass = null;
                try {
                    castClass = cast.getType();
                } catch (NotFoundException e) {}

                if (castClass != null && castClass.getName().equals("org.eclipse.core.resources.IContainer")) { //$NON-NLS-1$
                    cast.replace(
"$_ = null;" + //$NON-NLS-1$
"if (pathObject.getEntry() instanceof org.eclipse.core.resources.IContainer) {" + //$NON-NLS-1$
"    $_ = ($r) pathObject.getEntry();" + //$NON-NLS-1$
"} else {" + //$NON-NLS-1$
"    org.eclipse.core.resources.IResource resource = (org.eclipse.core.resources.IResource) pathObject.getEntry();" + //$NON-NLS-1$
"    includePath.add(resource.getFullPath().toOSString());" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                    );
                    checker.pass(PHPINIUTIL_CREATEPHPINIBYPROJECT_CAST_ICONTAINER);
                }
            }

            public void edit(MethodCall methodCall) throws CannotCompileException {
                if (methodCall.getClassName().equals("org.eclipse.core.resources.IContainer") //$NON-NLS-1$
                    && methodCall.getMethodName().equals("getLocation") //$NON-NLS-1$
                    ) {
                    methodCall.replace(
"$_ = null;" + //$NON-NLS-1$
"if (container != null) {" + //$NON-NLS-1$
"    $_ = $proceed($$);" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                        );
                    checker.pass(PHPINIUTIL_CREATEPHPINIBYPROJECT_CALL_GETLOCATION);
                } else if (methodCall.getMethodName().equals("modifyIncludePath")) { //$NON-NLS-1$
                    methodCall.replace(
"com.piece_framework.makegood.aspect.include_path_protection.PHPConfiguration phpConfiguration =" + //$NON-NLS-1$
"    new com.piece_framework.makegood.aspect.include_path_protection.PHPConfiguration();" + //$NON-NLS-1$
"String[] transformedIncludePaths = phpConfiguration.transformIncludePaths($1," + //$NON-NLS-1$
"                                                                          includePath,"+ //$NON-NLS-1$
"                                                                          project" + //$NON-NLS-1$
"                                                                          );" + //$NON-NLS-1$
"$_ = $proceed($1, transformedIncludePaths);" //$NON-NLS-1$
                        );
                    checker.pass(PHPINIUTIL_CREATEPHPINIBYPROJECT_CALL_MODIFYINCLUDEPATH);
                }
            }
        });
    }

    private void log(Exception e) {
        IStatus status = new Status(IStatus.ERROR,
                                    "com.piece_framework.makegood.aspect.include_path_protection", //$NON-NLS-1$
                                    0,
                                    e.getMessage(),
                                    e
                                    );
        Bundle bundle = Platform.getBundle("com.piece_framework.makegood.aspect.include_path_protection"); //$NON-NLS-1$
        Platform.getLog(bundle).log(status);
    }
}
