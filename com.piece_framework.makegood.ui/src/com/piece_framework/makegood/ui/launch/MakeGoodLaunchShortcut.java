/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2011-2012 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut;

import com.piece_framework.makegood.launch.Activator;
import com.piece_framework.makegood.launch.ProjectNotFoundException;
import com.piece_framework.makegood.launch.ResourceNotFoundException;
import com.piece_framework.makegood.launch.TestTargetRepository;

public class MakeGoodLaunchShortcut extends PHPExeLaunchShortcut {
    @Override
    protected ILaunchConfigurationType getPHPExeLaunchConfigType() {
        return DebugPlugin.getDefault()
                          .getLaunchManager()
                          .getLaunchConfigurationType("com.piece_framework.makegood.launch.makegoodLaunchConfigurationType"); //$NON-NLS-1$
    }

    /**
     * @since 1.3.0
     */
    protected void addTestTarget(Object testTarget) {
        try {
            TestTargetRepository.getInstance().add(testTarget);
        } catch (ResourceNotFoundException e) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            throw new TestLaunchException();
        } catch (ProjectNotFoundException e) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            throw new TestLaunchException();
        } catch (ModelException e) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            throw new TestLaunchException();
        }
    }

    /**
     * @since 1.3.0
     */
    protected void clearTestTargets() {
        TestTargetRepository.getInstance().clear();
    }
}
