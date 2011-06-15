/**
 * Copyright (c) 2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
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

import org.eclipse.core.resources.IProject;

import com.piece_framework.makegood.aspect.Aspect;
import com.piece_framework.makegood.aspect.PDTVersion;

/**
 * @since 1.2.0
 */
public class PHPexeItemRepositoryAspect extends Aspect {
    private static final String JOINPOINT_CREATE_SETBODY = "PHPexeItemRepository#create [set body]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_CREATE_SETBODY
    };
    private static final String WEAVINGCLASS_PHPEXEITEMREPOSITORY =
        "com.piece_framework.makegood.launch.PHPexeItemRepository"; //$NON-NLS-1$
    private static final String[] WEAVINGCLASSES = {
        WEAVINGCLASS_PHPEXEITEMREPOSITORY
    };

    /**
     * @see org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut#getDefaultPHPExe(IProject project)
     */
    private static final String PHPEXEITEMREPOSITORY_METHOD_CREATE_HELIOS =
"{" + //$NON-NLS-1$
"    org.eclipse.core.resources.IProject project = $1;" + //$NON-NLS-1$
"    org.eclipse.php.internal.debug.core.preferences.PHPexeItem phpexeItem = org.eclipse.php.internal.debug.core.PHPDebugPlugin.getPHPexeItem(project);" + //$NON-NLS-1$
"    if (phpexeItem == null) {" + //$NON-NLS-1$
"        return org.eclipse.php.internal.debug.core.PHPDebugPlugin.getWorkspaceDefaultExe();" + //$NON-NLS-1$
"    }" + //$NON-NLS-1$
"    return phpexeItem;" + //$NON-NLS-1$
"}"; //$NON-NLS-1$

    /**
     * @see org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut#getDefaultPHPExe(IProject project)
     * @see org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut#createPreferenceScopes(IProject project)
     */
    private static final String PHPEXEITEMREPOSITORY_METHOD_CREATE_GALILEO =
"{" + //$NON-NLS-1$
"    org.eclipse.core.resources.IProject project = $1;" + //$NON-NLS-1$
"    String phpDebuggerId = org.eclipse.php.internal.debug.core.PHPDebugPlugin.getCurrentDebuggerId();" + //$NON-NLS-1$
"    org.eclipse.php.internal.debug.core.preferences.PHPexeItem defaultItem = org.eclipse.php.internal.debug.core.preferences.PHPexes.getInstance().getDefaultItem(phpDebuggerId);" + //$NON-NLS-1$
"    if (defaultItem == null) return null;" + //$NON-NLS-1$
"    String phpExe = defaultItem.getName();" + //$NON-NLS-1$
"    if (project != null) {" + //$NON-NLS-1$
"        org.eclipse.core.runtime.preferences.IEclipsePreferences node = org.eclipse.php.internal.debug.core.preferences.PHPProjectPreferences.getProjectScope(project).getNode(org.eclipse.php.internal.debug.core.preferences.PHPProjectPreferences.getPreferenceNodeQualifier());" + //$NON-NLS-1$
"        if (node != null) {" + //$NON-NLS-1$
"            phpDebuggerId = node.get(org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames.PHP_DEBUGGER_ID, phpDebuggerId);" + //$NON-NLS-1$
"            phpExe = node.get(org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames.DEFAULT_PHP, phpExe);" + //$NON-NLS-1$
"        }" + //$NON-NLS-1$
"    }" + //$NON-NLS-1$
"    return org.eclipse.php.internal.debug.core.preferences.PHPexes.getInstance().getItem(phpDebuggerId, phpExe);" + //$NON-NLS-1$
"}"; //$NON-NLS-1$

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        CtClass weavingClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPEXEITEMREPOSITORY);
        CtMethod weavingMethod = weavingClass.getDeclaredMethod("create"); //$NON-NLS-1$
        weavingMethod.setBody(
            PDTVersion.getInstance().compareTo("2.2.0") >= 0 ? //$NON-NLS-1$
                PHPEXEITEMREPOSITORY_METHOD_CREATE_HELIOS :
                PHPEXEITEMREPOSITORY_METHOD_CREATE_GALILEO
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
