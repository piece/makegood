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

import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import com.piece_framework.makegood.javassist.Aspect;
import com.piece_framework.makegood.javassist.PDTVersion;

/**
 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=298606">Bug 298606 - Can't use the arguments with spaces.</a>
 */
public class CommandLineArgumentsFixAspect extends Aspect {
    private static final String JOINPOINT_CALL_SPLIT =
        "PHPLaunchUtilities#getProgramArguments() [call split()]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_CALL_SPLIT
    };
    private static final String WEAVINGCLASS_PHPLAUNCHUTILITIES =
        "org.eclipse.php.internal.debug.core.launching.PHPLaunchUtilities"; //$NON-NLS-1$
    private List<String> weavingClasses = new ArrayList<String>();

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        if (PDTVersion.getInstance().compareTo("2.2.0") >= 0) { //$NON-NLS-1$
            markJoinPointAsPassed(JOINPOINT_CALL_SPLIT);
            return;
        } else {
            weavingClasses.add(WEAVINGCLASS_PHPLAUNCHUTILITIES);
        }

        CtClass weavingClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPLAUNCHUTILITIES);
        weavingClass.getDeclaredMethod("getProgramArguments").instrument( //$NON-NLS-1$
            new ExprEditor() {
                @Override
                public void edit(MethodCall methodCall) throws CannotCompileException {
                    if (methodCall.getMethodName().equals("split")) { //$NON-NLS-1$
                        methodCall.replace(
"$_ = org.eclipse.debug.core.DebugPlugin.parseArguments($0);" //$NON-NLS-1$
                        );

                        markJoinPointAsPassed(JOINPOINT_CALL_SPLIT);
                    }
                }
            }
        );
        markClassAsWoven(weavingClass);
    }

    @Override
    protected String[] joinPoints() {
        return JOINPOINTS;
    }

    @Override
    protected String[] weavingClasses() {
        return weavingClasses.toArray(new String[ weavingClasses.size() ]);
    }
}
