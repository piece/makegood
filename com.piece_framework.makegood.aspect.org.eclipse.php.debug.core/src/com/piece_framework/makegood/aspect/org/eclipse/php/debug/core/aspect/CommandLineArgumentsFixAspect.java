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

package com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.piece_framework.makegood.javassist.Aspect;
import com.piece_framework.makegood.javassist.PreconditionViolationException;

/**
 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=298606">Bug 298606 - Can't use the arguments with spaces.</a>
 */
public class CommandLineArgumentsFixAspect extends Aspect {
    private static final String JOINPOINT_CALL_SPLIT =
        "PHPLaunchUtilities#getProgramArguments() [call split()]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_CALL_SPLIT
    };

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException, PreconditionViolationException {
        Bundle bundle = Platform.getBundle("org.eclipse.php.debug.core"); //$NON-NLS-1$
        if (bundle == null) {
            throw new PreconditionViolationException("The bundle org.eclipse.php.debug.core is not found."); //$NON-NLS-1$
        }

        if (bundle.getVersion().getMajor() < 2) {
            throw new PreconditionViolationException("The version of the bundle org.eclipse.php.debug.core must be >= 2. The current version is " + bundle.getVersion() + "."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (bundle.getVersion().compareTo(Version.parseVersion("2.2.0")) >= 0) { //$NON-NLS-1$
            pass(JOINPOINT_CALL_SPLIT);
            return;
        }

        CtClass weavingClass = ClassPool.getDefault().get("org.eclipse.php.internal.debug.core.launching.PHPLaunchUtilities"); //$NON-NLS-1$
        weavingClass.getDeclaredMethod("getProgramArguments").instrument( //$NON-NLS-1$
            new ExprEditor() {
                @Override
                public void edit(MethodCall methodCall) throws CannotCompileException {
                    if (methodCall.getMethodName().equals("split")) { //$NON-NLS-1$
                        methodCall.replace(
"$_ = org.eclipse.debug.core.DebugPlugin.parseArguments($0);" //$NON-NLS-1$
                        );

                        pass(JOINPOINT_CALL_SPLIT);
                    }
                }
            }
        );
        addWeavedClass(weavingClass);
    }

    @Override
    protected String[] joinPoints() {
        return JOINPOINTS;
    }
}
